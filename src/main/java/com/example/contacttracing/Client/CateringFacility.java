package com.example.contacttracing.Client;

import com.example.contacttracing.Controller;
import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class CateringFacility extends Controller {
    private final String name;
    private final String city;
    private final String phone;
    private final String busNum; // Business number
    private final String CF; // Unique identifier
    private String qrText;
    private Map<Integer,byte[]> pseudoKeys;


    private String generateQRString(byte[] todayPseudo) throws Exception {
        byte[] Ri = new byte[16];
        new SecureRandom().nextBytes(Ri);

        byte[] combined = new byte[Ri.length + todayPseudo.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < Ri.length ? Ri[i] : todayPseudo[i - Ri.length];
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined);

        return Arrays.toString(Ri) +"@"+CF+"@"+ Arrays.toString(hash);
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            Runnable getKeys = () -> {
                try {
                    if(pseudoKeys.isEmpty()) {
                        // Clear old keys and derive daily and pseudo keys for the coming days of the month
                        pseudoKeys = regImpl.getPseudoKeys(CF);
                    }

                    // Get pseudo key of the day
                    byte[] todayPseudo = pseudoKeys.remove(LocalDateTime.now().getMinute());
                    //  Generate daily QR string
                    qrText = generateQRString(todayPseudo);
                    System.out.println(qrText);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            // iedere minuut(=dag)
            executor.scheduleAtFixedRate(getKeys, 0, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CateringFacility(String n,String c,String p, String bN){
        this.name = n;
        this.city = c;
        this.phone = p;
        this.busNum = bN;
        this.CF = n+c+p+bN;
        this.pseudoKeys = new HashMap<>();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Name: ");
        String name = input.nextLine();
        System.out.println("City: ");
        String city = input.nextLine();
        System.out.println("Phone: ");
        String phone = input.nextLine();
        System.out.println("busNum: ");
        String busNum = input.nextLine();

        CateringFacility main = new CateringFacility(name, city, phone, busNum);
        main.run();
    }
}
