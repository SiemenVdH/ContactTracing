package com.example.contacttracing.Server.Registrar;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import javax.crypto.*;
import java.security.*;
import java.time.*;
import java.util.*;


public class RegistrarImpl extends UnicastRemoteObject implements RegistrarInterface {
    private final Registrar reg;
    private byte[] cipherText;


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
        ArrayList<byte[]> temp = reg.getDerivedDB().get(CF);
        for (byte[] bytes : temp) {
            byte[] hash = md.digest(bytes);
            pseudoQueue.add(hash);
        }
    }

    private void signTokens(byte[] random, byte day, ArrayList<byte[]> tokens) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException
    {
        for(int i=0; i<48; i++) {
            byte[] combined = new byte[random.length + day];
            for (int j = 0; j < combined.length; j++) {
                combined[j] = j < random.length ? random[j] : day;
            }

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(reg.getPrivateKey());
            signature.update(combined);
            tokens.add(signature.sign());
        }
    }

    public RegistrarImpl(Registrar r) throws RemoteException, NoSuchAlgorithmException {this.reg = r;}

    @Override
    public void deriveKeys(String CF) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException {
        if(reg.getDerivedDB().containsKey(CF) && reg.getPseudoDB().containsKey(CF)) {
            reg.removeDerivedDB(CF);
            reg.removePseudoDB(CF);
        }
        int daysInMonth = reg.getDaysOfMonth();
        int currentDay = LocalDateTime.now().getDayOfMonth();
        ArrayList<byte[]> derivedKeys = new ArrayList<>();
        makeDerivedKeys(CF, daysInMonth, currentDay, derivedKeys);
        reg.addToDerivedDB(CF, derivedKeys);
    }

    @Override
    public ArrayList<byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException {
        ArrayList<byte[]> pseudoQueue = new ArrayList<>();
        hashKeys(CF, pseudoQueue);
        reg.addToPseudoDB(CF, pseudoQueue);
        return reg.getPseudoDB().get(CF);
    }
    @Override
    public boolean enrolUser(String phone) throws RemoteException {
        if (!reg.getUsersDB().contains(phone)){
            reg.addUser(phone);
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<byte[]> getTokens(String phone, int today, byte[] random) throws RemoteException,
            NoSuchAlgorithmException, SignatureException, InvalidKeyException
    {
        if(reg.getUserTokensDB().containsKey(phone)) {
            reg.removeUserTokensDB(phone);
        }
        ArrayList<byte[]> tokens = new ArrayList<>();
        byte day = (byte) today;
        signTokens(random, day, tokens);
        reg.addToUserTokensDB(phone, tokens);
        return tokens;
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return reg.getPublicKey();
    }

    @Override
    public Map<String, ArrayList<byte[]>> getAllPseudos() throws RemoteException {
        return  reg.getPseudoDB();
    }
}
