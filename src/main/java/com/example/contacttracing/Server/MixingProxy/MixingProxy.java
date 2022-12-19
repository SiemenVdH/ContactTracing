package com.example.contacttracing.Server.MixingProxy;

import com.example.contacttracing.Shared.Capsule;
import com.example.contacttracing.Interfaces.MatchingInterface;
import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class MixingProxy {
    private PublicKey publicKeyRegistrar;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Map<LocalDateTime, Capsule> capsuleEntrys;


    private void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyGen.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }
    private Map<LocalDateTime, Capsule> shuffleCapsules() {
        List<Map.Entry<LocalDateTime,Capsule>> list
                = new ArrayList<>(capsuleEntrys.entrySet());
        Collections.shuffle(list);
        Map<LocalDateTime, Capsule> shuffledWindow = new LinkedHashMap<>();
        for (Map.Entry<LocalDateTime, Capsule> entry : list) {
            shuffledWindow.put(entry.getKey(), entry.getValue());
        }
        return shuffledWindow;
    }

    private void startServer(MixingProxy mixprox) {
        try {
            // create on port 4445
            Registry registry = LocateRegistry.createRegistry(4445);
            // create new services
            registry.rebind("MixingService", new MixingProxyImpl(mixprox));

            // fire to localhost port 4444
            Registry myRegistry1 = LocateRegistry.getRegistry("localhost", 4444);
            // search for Registrar service
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry1.lookup("RegistrarService");

            // fire to localhost port 4446
            Registry myRegistry2 = LocateRegistry.getRegistry("localhost", 4446);
            // search for Registrar service
            MatchingInterface matchImpl = (MatchingInterface) myRegistry2.lookup("MatchingService");

            this.publicKeyRegistrar = regImpl.getPublicKey();

            Runnable generateTokens = () -> {
                try {
                    Map<LocalDateTime, Capsule> shuffledMap = shuffleCapsules();
                    ArrayList<Capsule> capsules = new ArrayList<>(shuffledMap.values());
                    matchImpl.flushCapsules(capsules);
                    capsuleEntrys.clear();
                    System.out.println("Capsules flushed");
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            // iedere minuut(=dag)
            executor.scheduleAtFixedRate(generateTokens, 0, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("System is ready");
    }

    public MixingProxy() throws NoSuchAlgorithmException {
        this.capsuleEntrys = new HashMap<>();
        generateKeyPair();
    }
    public PublicKey getPublicKey() {return publicKey;}
    public PrivateKey getPrivateKey() {return privateKey;}
    public PublicKey getPublicKeyRegistrar() {return publicKeyRegistrar;}
    public void storeCapsule(LocalDateTime now, Capsule capsule) {capsuleEntrys.put(now, capsule);}

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MixingProxy main = new MixingProxy();
        main.startServer(main);
    }
}
