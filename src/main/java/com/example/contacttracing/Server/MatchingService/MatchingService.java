package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Interfaces.RegistrarInterface;
import com.example.contacttracing.Shared.Capsule;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class MatchingService {
    private ArrayList<Capsule> capsules;
    private Map<String, byte[]> dailyPseudoDB;
    private ArrayList<String[]> userLogValues;
    private ArrayList<Capsule> critical;
    private ArrayList<byte[]> informed;


    private byte[] checkHash(byte[] Ri, byte[] dailyPseudo) throws NoSuchAlgorithmException {
        byte[] combined = new byte[Ri.length + dailyPseudo.length];
        for (int j = 0; j < combined.length; ++j) {
            combined[j] = j < Ri.length ? Ri[j] : dailyPseudo[j - Ri.length];
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined);

        return hash;
    }

    private void startServer(MatchingService matchserv) {
        try {
            // create on port 4446
            Registry registry = LocateRegistry.createRegistry(4446);
            // create new services
            registry.rebind("MatchingService", new MatchingServiceImpl(matchserv));
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            Runnable generateTokens = () -> {
                try {
                    dailyPseudoDB = regImpl.getPseudosFromDay(LocalDateTime.now().getMinute());
                    System.out.println("Day specific pseudos received");
                    System.out.println(dailyPseudoDB);
                    /*for(int i=0; i<userLogValues.size(); i++) {
                        byte[] Ri = userLogValues.get(i)[0].getBytes();
                        byte[] hash = userLogValues.get(i)[1].getBytes();
                        byte[] dailyPseudo = dailyPseudoDB.get();

                        if(checkHash(Ri, dailyPseudo).equals(hash)) {
                            String CF = userLogValues.get(i)[3];
                            String interval = userLogValues.get(i)[2];


                        }
                    }*/
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                // capsules.clear();// delete data from db after certain time
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            // iedere minuut(=dag)
            executor.scheduleAtFixedRate(generateTokens, 0, 60, TimeUnit.SECONDS);

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
        System.out.println("Capsules: "+capsules.toString());
    }

    public void addLogs(String logData) {
        String[] data = logData.split("/");
        // data[0] = Ri, data[1] = hash, data[2] = Interval, data[3] =CF, data[4] =dailyToken
        userLogValues.add(data);
        System.out.println("UserLogDB: "+userLogValues);
        informed.add(data[4].getBytes());
        System.out.println("Informed tokens: "+informed);
    }

    public static void main(String[] args) {
        MatchingService main = new MatchingService();
        main.startServer(main);
    }
}
