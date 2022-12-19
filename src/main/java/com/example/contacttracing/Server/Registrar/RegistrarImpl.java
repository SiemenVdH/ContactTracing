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


    private Map<Integer, byte[]> deriveKeys(String CF) throws NoSuchAlgorithmException, InvalidKeyException {
        /*int daysInMonth = reg.getDaysOfMonth();
        int currentDay = LocalDateTime.now().getDayOfMonth();*/
        Map<Integer,byte[]> derivedKeys = new HashMap<>();

        // Maak derived keyset voor komende 30 minuten (zogezegd 1 maand)
        for(int i=0; i<30; i++) {
            Mac mac = reg.getMasterKey();
            int specificDay = LocalDateTime.now().plusMinutes(i).getMinute();
            String input = CF+specificDay;
            cipherText = mac.doFinal(input.getBytes());
            derivedKeys.put(specificDay,cipherText);
        }
        reg.addToDerivedDB(CF, derivedKeys);
        return derivedKeys;
    }

    private Map<Integer, byte[]> hashKeys(Map<Integer, byte[]> dKeys) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Map<Integer, byte[]> pseudoQueue = new HashMap<>();
        for(Integer day : dKeys.keySet()) {
            byte[] hash = md.digest(dKeys.get(day));
            pseudoQueue.put(day,hash);
        }
        return pseudoQueue;
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

    public RegistrarImpl(Registrar r) throws RemoteException {this.reg = r;}

    @Override
    public Map<Integer,byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException,
            InvalidKeyException
    {
        Map<Integer, byte[]> dKeys = deriveKeys(CF);
        Map<Integer, byte[]> pseudoQueue = hashKeys(dKeys);
        reg.addToPseudoDB(CF, pseudoQueue);
        return pseudoQueue;
    }
    @Override
    public boolean enrolUser(String phone) throws RemoteException {
        if (!phone.isBlank() && !reg.getUsersDB().contains(phone)){
            reg.addUser(phone);
            return false;
        }
        else return true;
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
    public Map<String, byte[]> getPseudosFromDay(int day) throws RemoteException {
        Map<String, byte[]> daySpecificPseudos = new HashMap<>();
        for(String CF : reg.getPseudoDB().keySet()) {
            daySpecificPseudos.put(CF, reg.getPseudoDB().get(CF).get(day));
        }
        return daySpecificPseudos;
    }
}
