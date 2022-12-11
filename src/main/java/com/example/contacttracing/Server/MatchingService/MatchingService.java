package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Shared.Capsule;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchingService {
    private ArrayList<Capsule> capsules;

    private void startServer() {
        try {
            // create on port 6666
            Registry registry = LocateRegistry.createRegistry(4446);
            // create new services
            registry.rebind("MatchingService", new MatchingServiceImpl());

            Runnable generateTokens = () -> {
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
    }

    public void addToCapsules(ArrayList<Capsule> mixingCapsules) {
        capsules = mixingCapsules;
    }

    public static void main(String[] args) {
        MatchingService main = new MatchingService();
        main.startServer();
    }
}
