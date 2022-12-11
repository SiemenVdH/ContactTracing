package com.example.contacttracing.Server.Registrar;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import javax.crypto.*;
import java.security.*;
import java.time.*;
import java.util.*;


public class RegistrarImpl extends UnicastRemoteObject implements RegistrarInterface {
    private Registrar reg;
    private byte[] cipherText;
    private Map<String, ArrayList<byte[]>> derivedDB;
    private Map<String, ArrayList<byte[]>> pseudoDB;
    private Map<String, ArrayList<byte[]>> userTokensDB;
    private ArrayList<String> usersDB;


    private void makeDerivedKeys(String CF, int daysInMonth, int currentDay, ArrayList<byte[]> derivedKeys) throws
            NoSuchAlgorithmException, InvalidKeyException
    {
        for(int i=0; i<(daysInMonth-currentDay); i++) {
            Mac mac = reg.getMasterKey();
            String specificDay = String.valueOf(LocalDateTime.now().plusDays(i).getDayOfMonth());
            String input = CF+specificDay;
            cipherText = mac.doFinal(input.getBytes());
            derivedKeys.add(cipherText);
        }
    }

    private void hashKeys(String CF, ArrayList<byte[]> pseudoQueue) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        ArrayList<byte[]> temp = derivedDB.get(CF);
        for (byte[] bytes : temp) {
            byte[] hash = md.digest(bytes);
            pseudoQueue.add(hash);
        }
    }

    private void signTokens(byte day, ArrayList<byte[]> tokens) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException
    {
        for(int i=0; i<48; i++) {
            byte[] random = new byte[16];
            new SecureRandom().nextBytes(random);

            byte[] combined = new byte[random.length + day];
            for (int j = 0; j < combined.length; j++) {
                combined[j] = j < random.length ? random[j] : day;
            }

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(reg.getPrivateKey());
            signature.update(combined);
            byte[] digitalSignature = signature.sign();
            tokens.add(digitalSignature);
        }
    }

    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        this.reg = new Registrar();
        this.derivedDB = new HashMap<>();
        this.pseudoDB = new HashMap<>();
        this.userTokensDB = new HashMap<>();
        this.usersDB = new ArrayList<>();
    }

    @Override
    public void deriveKeys(String CF) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException {
        int daysInMonth = reg.getDayOfMonth();
        int currentDay = LocalDateTime.now().getDayOfMonth();
        ArrayList<byte[]> derivedKeys = new ArrayList<>();
        makeDerivedKeys(CF, daysInMonth, currentDay, derivedKeys);
        derivedDB.put(CF, derivedKeys);
    }

    @Override
    public ArrayList<byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException {
        ArrayList<byte[]> pseudoQueue = new ArrayList<>();
        hashKeys(CF, pseudoQueue);
        pseudoDB.put(CF, pseudoQueue);
        return pseudoDB.get(CF);
    }

    @Override public void clearDailyAndPseudoKeys(String CF) throws RemoteException {
        if(derivedDB.containsKey(CF) && pseudoDB.containsKey(CF)) {
            derivedDB.remove(CF);
            pseudoDB.remove(CF);
        }
    }

    @Override
    public void clearDailyTokens(String phone) throws RemoteException {
        if(userTokensDB.containsKey(phone)) {userTokensDB.remove(phone);}
    }

    @Override
    public boolean enrolUser(String phone) throws RemoteException {
        if (!usersDB.contains(phone)){
            usersDB.add(phone);
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<byte[]> getTokens(String phone, int today) throws RemoteException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        ArrayList<byte[]> tokens = new ArrayList<>();
        //System.out.println("tokens");
        byte day = (byte) today;
        signTokens(day, tokens);
        userTokensDB.put(phone, tokens);
        return tokens;
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return reg.getPublicKey();
    }
}
