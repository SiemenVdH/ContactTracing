package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Interfaces.RegistrarInterface;
import com.example.contacttracing.Shared.Capsule;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchingService {
    private ArrayList<Capsule> capsules;
    private Map<String, ArrayList<byte[]>> dailyPseudoDB;

    private void startServer() {
        try {
            // create on port 6666
            Registry registry = LocateRegistry.createRegistry(4446);
            // create new services
            registry.rebind("MatchingService", new MatchingServiceImpl());
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            Runnable generateTokens = () -> {
                try {
                    dailyPseudoDB = regImpl.getAllPseudos();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                capsules.clear();
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(generateTokens, 0, 5, TimeUnit.SECONDS);  // iedere dag


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("System is ready");
    }

    public MatchingService() {
        this.capsules = new ArrayList<>();
        this.dailyPseudoDB = new HashMap<>();
    }

    public void addToCapsules(ArrayList<Capsule> mixingCapsules) {
        capsules = mixingCapsules;
    }

    public static void main(String[] args) {
        MatchingService main = new MatchingService();
        main.startServer();
    }
}
