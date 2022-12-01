package com.example.contacttracing.Server.MatchingService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;

public class MatchingService {

    private void startServer() {
        try {
            // create on port 4444
            Registry registry = LocateRegistry.createRegistry(4445);
            // create new services
            registry.rebind("MatchingService", new MatchingServiceImpl());


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("System is ready");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MatchingService main = new MatchingService();
        main.startServer();
    }
}
