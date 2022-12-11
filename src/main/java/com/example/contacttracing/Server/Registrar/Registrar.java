package com.example.contacttracing.Server.Registrar;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.crypto.*;
import java.security.*;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Registrar {
    private SecretKey masterKey;
    private PrivateKey privateKey;
    public PublicKey publicKey;
    private Map<String, ArrayList<byte[]>> derivedDB;
    private Map<String, ArrayList<byte[]>> pseudoDB;
    private Map<String, ArrayList<byte[]>> userTokensDB;
    private ArrayList<String> usersDB;


    private void generateMasterKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        this.masterKey = keyGenerator.generateKey();
    }
    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
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
    private void startServer(Registrar reg) {
        try {
            // create on port 4444
            Registry registry = LocateRegistry.createRegistry(4444);
            // create new services
            registry.rebind("RegistrarService", new RegistrarImpl(reg));

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public Registrar() throws NoSuchAlgorithmException {
        this.derivedDB = new HashMap<>();
        this.pseudoDB = new HashMap<>();
        this.userTokensDB = new HashMap<>();
        this.usersDB = new ArrayList<>();
        generateMasterKey();
        generateKeyPair();
    }
    public Mac getMasterKey() throws NoSuchAlgorithmException, InvalidKeyException {return getMacKey();}
    public PublicKey getPublicKey() {return this.publicKey;}
    public PrivateKey getPrivateKey() {return this.privateKey;}
    public int getDaysOfMonth() {
        int month = LocalDateTime.now().getMonthValue();
        int year = LocalDateTime.now().getYear();
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }
    public Map<String, ArrayList<byte[]>> getDerivedDB() {return derivedDB;}
    public void addToDerivedDB(String CF, ArrayList<byte[]> derivedKeys) {derivedDB.put(CF, derivedKeys);}
    public void removeDerivedDB(String CF) {derivedDB.remove(CF);}
    public Map<String, ArrayList<byte[]>> getPseudoDB() {return pseudoDB;}
    public void addToPseudoDB(String CF, ArrayList<byte[]> pseudoQueue) {pseudoDB.put(CF, pseudoQueue);}
    public void removePseudoDB(String CF) {pseudoDB.remove(CF);}
    public Map<String, ArrayList<byte[]>> getUserTokensDB() {return userTokensDB;}
    public void addToUserTokensDB(String phone, ArrayList<byte[]> tokens) {userTokensDB.put(phone, tokens);}
    public void removeUserTokensDB(String phone) {userTokensDB.remove(phone);}
    public ArrayList<String> getUsersDB() {return usersDB;}
    public void addUser(String phone) {usersDB.add(phone);}

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Registrar main = new Registrar();
        main.startServer(main);
    }

}
