package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.MixingInterface;
import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.security.*;
import java.time.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class UserApp {
    private final String phone;
    private ArrayList<byte[]> dailyTokens;
    private Map<LocalDateTime, Log> logValues;
    private RegistrarInterface regImpl;
    private MixingInterface mixImpl;


    private Log readQR(String qrText) {
        String[] extracted = qrText.split("@");
        Log log = new Log(extracted[0], extracted[1], extracted[2], LocalDateTime.now());
        logValues.put(LocalDateTime.now(), log);
        return log;
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry1 = LocateRegistry.getRegistry("localhost", 4444);
            // search for Registrar service
            regImpl = (RegistrarInterface) myRegistry1.lookup("RegistrarService");
            // fire to localhost port 5555
            Registry myRegistry2 = LocateRegistry.getRegistry("localhost", 5555);
            // search for Mixing service
            mixImpl = (MixingInterface) myRegistry2.lookup("MixingService");

            int today = LocalDateTime.now().getDayOfMonth();

            if(regImpl.enrolUser(phone)) {
                System.out.println("User is already enrolt!");
            }
            else {
                System.out.println("Succesfully enrolt!");

                Runnable generateTokens = () -> {
                    try {
                        dailyTokens = regImpl.getTokens(phone, today);
                    } catch (RemoteException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                };
                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(generateTokens, 0, 5, TimeUnit.SECONDS);   //iedere dag
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerEntry() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Log log = readQR("");
        Capsule capsule = new Capsule(dailyTokens.remove(0), log.getIneterval(), log.getHash());
        mixImpl.sendCapsule(capsule);
    }

    public UserApp(String p){
        this.phone = p;
        this.dailyTokens = new ArrayList<>();
        this.logValues = new HashMap<>();
        run();
    }
}