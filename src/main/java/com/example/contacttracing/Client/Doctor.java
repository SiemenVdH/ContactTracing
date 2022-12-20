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
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private ArrayList<String> allLogs;


    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    private void readLog(File myObj) {
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                // 1 log input consists of 2 lines
                String data1 = myReader.nextLine();
                String data2 = myReader.nextLine();
                // System.out.println(data1+data2);
                allLogs.add(data1+data2);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private byte[] signLogs(String logs) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(logs.getBytes());
        return signature.sign();
    }

    private void forwardLogs(MatchingInterface matchImpl) throws NoSuchAlgorithmException, SignatureException,
            InvalidKeyException, RemoteException
    {
        Collections.shuffle(allLogs);
        for(String log: allLogs) {
            byte[] signedLog = signLogs(log);
            boolean succeeded = matchImpl.forwardLogs(publicKey, signedLog, log);
            if (!succeeded) System.out.println("Invalid forwarding");
            else System.out.println("Log forwarded");
        }
        allLogs.clear();
    }

    private void run() {
        try {
            // fire to localhost port 4446
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4446);
            // search for SendService & ReceiveService
            MatchingInterface matchImpl = (MatchingInterface) myRegistry.lookup("MatchingService");

            File myObj = new File("log.txt");
            Runnable readLogTxt = () -> {
                if(myObj.exists()) {
                    readLog(myObj);
                    System.out.println("Log.txt file read");
                    try {
                        forwardLogs(matchImpl);
                    } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("All logs successfully forwarded");
                    myObj.delete();
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(readLogTxt, 0, 30, TimeUnit.SECONDS);  // keep running


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Doctor() throws NoSuchAlgorithmException {
        generateKeyPair();
        this.allLogs = new ArrayList<>();
    }

    public static void main (String[] args) throws NoSuchAlgorithmException {
        Doctor main = new Doctor();
        main.run();
    }
}
