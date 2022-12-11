package com.example.contacttracing.Client;

import com.example.contacttracing.Controller;
import com.example.contacttracing.Interfaces.MixingInterface;
import com.example.contacttracing.Interfaces.RegistrarInterface;
import com.example.contacttracing.Shared.Capsule;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.security.*;
import java.time.*;
import java.util.ArrayList;
import java.util.concurrent.*;

public class UserApp extends Controller {
    private final String phone;
    private static ArrayList<byte[]> dailyTokens;
    private static Map<LocalDateTime, Log> logValues;
    private RegistrarInterface regImpl;
    private static MixingInterface mixImpl;
    private static byte[] random;



    private byte[] generateRandomValue() {
        byte[] random = new byte[16];
        new SecureRandom().nextBytes(random);
        return random;
    }

    private static Log readQR(String qrText) {
        String[] extracted = qrText.split("@");
        Log log = new Log(extracted[0], extracted[1], extracted[2], LocalDateTime.now(), dailyTokens.remove(0));
        logValues.put(LocalDateTime.now(), log);
        return log;
    }

    private static boolean confirmSignature(byte[] digitalSignature, Capsule capsule) throws NoSuchAlgorithmException,
            RemoteException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(mixImpl.getPublicKey());
        signature.update(capsule.getHash().getBytes());
        return signature.verify(digitalSignature);
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry1 = LocateRegistry.getRegistry("localhost", 4444);
            // search for Registrar service
            regImpl = (RegistrarInterface) myRegistry1.lookup("RegistrarService");
            // fire to localhost port 5556
            Registry myRegistry2 = LocateRegistry.getRegistry("localhost", 4445);
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
                        random = generateRandomValue();
                        dailyTokens = regImpl.getTokens(phone, today, random);
                        clearOldLogValues();
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

    public static void registerEntry(String qr) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, RemoteException {
        Log log = readQR(qr);
        Capsule capsule = new Capsule(log.getDailyToken(), log.getInterval(), log.getHash(), random);
        byte[] digitalConfirmation = mixImpl.sendCapsule(capsule);
        boolean confirmed = confirmSignature(digitalConfirmation, capsule);
        if(confirmed) {
            //Controller.Polyline.set(digitalConfirmation);
        }
    }

    public static void clearOldLogValues() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException{
        LocalDateTime today = LocalDateTime.now();
        for(LocalDateTime e: logValues.keySet())
            if(Duration.between(e, today).toDays()>10)   // entries ouder dan 10 dagen
                logValues.remove(e);
    }

    public static void printLogs() {
        for(int i=0; i<logValues.size(); i++){
            logValues.get(i).writeToFile();
        }
    }

    public UserApp(String p){
        this.phone = p;
        this.dailyTokens = new ArrayList<>();
        this.logValues = new HashMap<>();
        run();
    }
}