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

    @Override
    public void deriveKeys(String CF) throws RemoteException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException
    {
        int daysInMonth = reg.getDayOfMonth();
        int currentday = LocalDateTime.now().getDayOfMonth();
        ArrayList<byte[]> derivedKeys = new ArrayList<>();

        for(int i=0; i<(daysInMonth-currentday); i++) {
            Cipher cipher = reg.getMasterKey();
            String specificDay = String.valueOf(LocalDateTime.now().plusDays(i).getDayOfMonth());
            String input = CF+specificDay;
            cipherText = cipher.doFinal(input.getBytes());
            derivedKeys.add(cipherText);
        }
        derivedDB.put(CF, derivedKeys);
    }

    @Override
    public ArrayList<byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        ArrayList<byte[]> temp = derivedDB.get(CF);
        ArrayList<byte[]> pseudoQueue = new ArrayList<>();

        for(int i=0; i<temp.size(); i++) {
            byte[] hash = md.digest(temp.get(i));
            pseudoQueue.add(hash);
        }
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
