package com.example.contacttracing.Server.Registrar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Registrar {
    private SecretKey masterKey;

    public SecretKey getMasterKey() {
        return masterKey;
    }

    private void generateMasterKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        masterKey = keyGenerator.generateKey();

        byte[] initVect = new byte[16];
        new SecureRandom().nextBytes(initVect);
        IvParameterSpec iv = new IvParameterSpec(initVect);
    }

    private void startServer() {
        try {
            // create on port 4444
            Registry registry = LocateRegistry.createRegistry(4444);
            // create new services
            registry.rebind("RegistrarService", new RegistrarImpl());

            generateMasterKey();
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
