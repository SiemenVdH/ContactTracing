package com.example.contacttracing.Server.Registrar;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.security.*;

public class Registrar {
    private SecretKey masterKey;

    public Registrar() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        this.masterKey = keyGenerator.generateKey();
    }

    public Cipher getMasterKey() throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException
    {
        byte[] initVect = new byte[16];
        new SecureRandom().nextBytes(initVect);
        IvParameterSpec iv = new IvParameterSpec(initVect);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, iv);
        return cipher;
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

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Registrar main = new Registrar();
        main.startServer();
    }

}
