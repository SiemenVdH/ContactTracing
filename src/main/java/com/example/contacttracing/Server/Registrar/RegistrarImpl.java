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

    public RegistrarImpl() throws RemoteException, NoSuchAlgorithmException {
        this.reg = new Registrar();
        this.derivedDB = new HashMap<>();
        this.pseudoDB = new HashMap<>();
    }

    private void makeDerivedKeys(String CF, int daysInMonth, int currentDay, ArrayList<byte[]> derivedKeys) throws
            InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException
    {
        for(int i=0; i<(daysInMonth-currentDay); i++) {
            Cipher cipher = reg.getMasterKey();
            String specificDay = String.valueOf(LocalDateTime.now().plusDays(i).getDayOfMonth());
            String input = CF+specificDay;
            cipherText = cipher.doFinal(input.getBytes());
            derivedKeys.add(cipherText);
        }
    }

    private void hashKeys(String CF, ArrayList<byte[]> pseudoQueue) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        ArrayList<byte[]> temp = derivedDB.get(CF);
        for(int i=0; i<temp.size(); i++) {
            byte[] hash = md.digest(temp.get(i));
            pseudoQueue.add(hash);
        }
    }

    @Override
    public void deriveKeys(String CF) throws RemoteException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException
    {
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
}
