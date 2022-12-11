package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.security.*;
import java.time.*;
import java.util.*;

public class CateringFacility {
    private String name;
    private String city;
    private String phone;
    private String busNum; // Business number
    private String CF; // Unique identifier
    private String qrText;
    private ArrayList<byte[]> pseudoKeys;


    private String generateQRString(byte[] todayPseudo) throws Exception {
        byte[] Ri = new byte[16];
        new SecureRandom().nextBytes(Ri);

        byte[] combined = new byte[Ri.length + todayPseudo.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < Ri.length ? Ri[i] : todayPseudo[i - Ri.length];
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined);

        String qrStr = Ri.toString()+"@"+CF+"@"+hash.toString();
        return qrStr;
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            int today = LocalDateTime.now().getDayOfMonth();
            boolean firstDay = true; // generate QR on start day


            while(true) {
                if(pseudoKeys.isEmpty()) {
                    // Clear old keys
                    regImpl.clearDailyAndPseudoKeys(CF);
                    // Derive daily and pseudo keys for the coming 30 days
                    regImpl.deriveKeys(CF);
                    pseudoKeys = regImpl.getPseudoKeys(CF);
                }

                if(firstDay || LocalDateTime.now().getDayOfMonth()!= today) {
                    today = LocalDateTime.now().getDayOfMonth();
                    firstDay = false;
                    // Get pseudo key of the day
                    byte[] todayPseudo = pseudoKeys.remove(0);
                    //  Generate daily QR string and code
                    qrText = generateQRString(todayPseudo);
                    System.out.println(qrText);
                    // generateQR(qrText, System.getProperty("user.dir"));
                }
            }
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
        this.pseudoKeys = new ArrayList<>();
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
