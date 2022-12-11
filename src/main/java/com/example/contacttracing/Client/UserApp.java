package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserApp {
    private final String phone;
    private ArrayList<byte[]> dailyTokens;


    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            int today = LocalDateTime.now().getDayOfMonth();
            // generate QR on start day

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

    public UserApp(String p){
        this.phone = p;
        this.dailyTokens = new ArrayList<>();
        run();
    }
}