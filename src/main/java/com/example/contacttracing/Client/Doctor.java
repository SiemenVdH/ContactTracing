package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.MatchingInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.*;
import java.util.Scanner;


public class Doctor {
    private PrivateKey privateKey;
    private PublicKey publicKey;


    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
    private byte[] signLogs() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update();
        return signature.sign();
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4446);
            // search for SendService & ReceiveService
            MatchingInterface mathImpl = (MatchingInterface) myRegistry.lookup("MatchingService");

            signLogs();
            mathImpl.forwardLogs(publicKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Doctor() throws NoSuchAlgorithmException {generateKeyPair();}

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Scanner input = new Scanner(System.in);
        System.out.println("Phone of sick patient: ");
        String phone = input.nextLine();

        Doctor main = new Doctor();
        main.run();
    }
}
