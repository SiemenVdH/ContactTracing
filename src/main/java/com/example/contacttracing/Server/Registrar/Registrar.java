package com.example.contacttracing.Server.Registrar;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.crypto.*;
import java.security.*;
import java.time.*;

public class Registrar {
    private SecretKey masterKey;
    private PrivateKey privateKey;
    private PublicKey publicKey;


    private void generateMasterKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        this.masterKey = keyGenerator.generateKey();
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    private Mac getMacKey() throws NoSuchAlgorithmException, InvalidKeyException {
        /* Less secure and complexer to use!
        byte[] initVect = new byte[16];
        new SecureRandom().nextBytes(initVect);
        IvParameterSpec iv = new IvParameterSpec(initVect);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, iv);
        return cipher;*/
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(masterKey);
        return mac;
    }

    private void startServer() {
        try {
            // create on port 4444
            Registry registry = LocateRegistry.createRegistry(4444);
            // create new services
            registry.rebind("RegistrarService", new RegistrarImpl());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public Registrar() throws NoSuchAlgorithmException {
        generateMasterKey();
        generateKeyPair();
    }

    public Mac getMasterKey() throws NoSuchAlgorithmException, InvalidKeyException {return getMacKey();}

    public PublicKey getPublicKey() {return publicKey;}
    public PrivateKey getPrivateKey() {return privateKey;}

    public int getDayOfMonth() {
        int month = LocalDateTime.now().getMonthValue();
        int year = LocalDateTime.now().getYear();
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Registrar main = new Registrar();
        main.startServer();
    }

}
