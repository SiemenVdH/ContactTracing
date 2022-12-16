package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Interfaces.RegistrarInterface;
import com.example.contacttracing.Shared.Capsule;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.util.concurrent.*;

public class MatchingService {
    private ArrayList<Capsule> capsules;
    private Map<String, ArrayList<byte[]>> dailyPseudoDB;
    private ArrayList<String[]> userLogValues;
    private ArrayList<String[]> critical;
    private ArrayList<byte[]> informed;
    private ArrayList<byte[]> uninformed;


    private void startServer(MatchingService matchserv) {
        try {
            // create on port 6666
            Registry registry = LocateRegistry.createRegistry(4446);
            // create new services
            registry.rebind("MatchingService", new MatchingServiceImpl(matchserv));
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
        this.userLogValues = new ArrayList<>();
    }

    public void addToCapsules(ArrayList<Capsule> mixingCapsules) {
        capsules = mixingCapsules;
    }

    public void addLogs(String logData) {
        String[] data = logData.split("/");
        // data[0] = Ri, data[1] = hash, data[2] = Interval, data[3] =dailyToken
        userLogValues.add(data);
    }

    public static void main(String[] args) {
        MatchingService main = new MatchingService();
        main.startServer(main);
    }
}
