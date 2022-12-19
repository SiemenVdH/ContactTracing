package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.MatchingInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Doctor {
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static ArrayList<String> allLogs = new ArrayList<>();
    private static MatchingInterface mathImpl;


    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    private static void readLog() {
        try {
            File myObj = new File("log.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                // 1 log input consists of 2 lines
                String data1 = myReader.nextLine();
                String data2 = myReader.nextLine();
                System.out.println(data1+data2);
                allLogs.add(data1+data2);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static byte[] signLogs(String logs) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(logs.getBytes());
        return signature.sign();
    }

    private void run() {
        try {
            // fire to localhost port 4446
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4446);
            // search for SendService & ReceiveService
            mathImpl = (MatchingInterface) myRegistry.lookup("MatchingService");

            Runnable generateTokens = () -> {
                System.out.println("is empty");
                if(!allLogs.isEmpty()) {
                    System.out.println("is not empty");
                    Collections.shuffle(allLogs);
                    for(String logs: allLogs) {
                        try {
                            byte[] signedLog = signLogs(logs);
                            mathImpl.forwardLogs(publicKey, signedLog, logs);
                        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | RemoteException e) {
                            throw new RuntimeException(e);
                        }
                        allLogs.remove(logs);
                    }
                    System.out.println("Logs forwarded");
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(generateTokens, 0, 30, TimeUnit.SECONDS);  // keep running


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Doctor() throws NoSuchAlgorithmException {
        generateKeyPair();
    }

    public static void readLogs() {
        readLog();
        System.out.println("Log file successfully received");
    }

    public static void main (String[] args) throws NoSuchAlgorithmException {
        Doctor main = new Doctor();
        main.run();
    }
}
