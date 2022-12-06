package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.security.*;
import java.time.*;
import java.util.*;

public class CateringFacility {
    private String name;
    private String city;
    private String phone;
    private String busNum; // Business number
    private String CF; // Unique identifier
    private ArrayList<byte[]> pseudoKeys;

    public CateringFacility(String n,String c,String p, String bN){
        this.name = n;
        this.city = c;
        this.phone = p;
        this.busNum = bN;
        this.CF = n+c+p+bN;
        this.pseudoKeys = new ArrayList<>();
    }

    private String generateQRString(byte[] todayPseudo) throws Exception {
        byte[] Ri = new byte[16];
        new SecureRandom().nextBytes(Ri);

        byte[] combined = new byte[Ri.length + todayPseudo.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < Ri.length ? Ri[i] : todayPseudo[i - Ri.length];
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(combined);

        return Ri.toString()+CF+hash.toString();
    }

    public void generateQR(String text) throws WriterException, IOException {
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix matrix = new MultiFormatWriter().encode(new String(text.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, 200, 200);
        //BitMatrix to javaFx
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
                    String qrText = generateQRString(todayPseudo);
                    generateQR(qrText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
