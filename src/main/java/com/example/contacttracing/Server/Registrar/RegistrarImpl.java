package com.example.contacttracing.Server.Registrar;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.time.LocalDateTime;

import java.util.ArrayList;


public class RegistrarImpl extends UnicastRemoteObject implements RegistrarInterface {
    Registrar reg = new Registrar();
    byte[] cipherText;
    ArrayList<byte[]> derivedKeys;
    ArrayList<byte[]> hashedQueue;

    public RegistrarImpl() throws RemoteException {}

    @Override
    public void deriveKeys(String CF) throws RemoteException, InvalidAlgorithmParameterException,
            NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException
    {
        for(int i=0; i<30; i++) {
            Cipher cipher = reg.getMasterKey();
            String specificDay = String.valueOf(LocalDateTime.now().plusDays(i).getDayOfMonth());
            String input = CF+specificDay;
            cipherText = cipher.doFinal(input.getBytes());
            derivedKeys.add(cipherText);
        }
    }

    @Override
    public ArrayList<byte[]> getPseudoKeys() throws RemoteException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for(int i=0; i<derivedKeys.size(); i++) {
            byte[] hash = md.digest(derivedKeys.get(i));
            hashedQueue.add(hash);
        }
        return hashedQueue;
    }
}
