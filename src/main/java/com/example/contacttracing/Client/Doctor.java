package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.MatchingInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Doctor {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private ArrayList<String> allLogs;


    private void readLog() {
        try {
            File myObj = new File("log.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                allLogs.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
    private byte[] signLogs(String logs) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
            MatchingInterface mathImpl = (MatchingInterface) myRegistry.lookup("MatchingService");

            readLog();
            for(String logs: allLogs) {
                mathImpl.forwardLogs(publicKey, signLogs(logs), logs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Doctor() throws NoSuchAlgorithmException {
        this.allLogs = new ArrayList<>();
        generateKeyPair();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Doctor main = new Doctor();
        main.run();
    }
}
