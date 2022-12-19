package com.example.contacttracing.Client;

import com.example.contacttracing.Controller;
import com.example.contacttracing.Interfaces.MixingInterface;
import com.example.contacttracing.Interfaces.RegistrarInterface;
import com.example.contacttracing.Shared.Capsule;

import java.io.*;
import java.nio.ByteBuffer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.*;
import java.security.*;
import java.time.*;
import java.util.ArrayList;
import java.util.concurrent.*;


public class UserApp extends Controller implements Serializable {
    private final String phone;
    private static String enrollStatus;
    private static ArrayList<byte[]> dailyTokens;
    private static Map<LocalDateTime, Log> logValues;
    private static Log tempLog;
    private static MixingInterface mixImpl;
    private static byte[] random;


    private static byte[] generateRandomValue() {
        byte[] random = new byte[16];
        new SecureRandom().nextBytes(random);
        return random;
    }

    private static void clearOldLogValues() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException{
        LocalDateTime today = LocalDateTime.now();
        for(LocalDateTime e: logValues.keySet()) {
            if (Duration.between(e, today).toMinutes() > 5) {  // entries ouder dan 5 minuten
                logValues.remove(e);
                System.out.println("Old log file cleared");
            }
        }
    }

    private static void readQR(String qrText) {
        String[] extracted = qrText.split("@");
        LocalDateTime now = LocalDateTime.now();
        // extracted[0] = Ri, extracted[1] = CF, extracted[2] = hash
        tempLog = new Log(extracted[0], extracted[1], extracted[2], now, dailyTokens.remove(0));
        logValues.put(now, tempLog);
    }

    private static boolean confirmSignature(byte[] digitalSignature, Capsule capsule) throws NoSuchAlgorithmException,
            RemoteException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(mixImpl.getPublicKey());
        signature.update(capsule.getHash().getBytes());
        return signature.verify(digitalSignature);
    }

    private static String[] getColor(byte[] digitalConfirmation) {
        ByteBuffer bytebuf = ByteBuffer.wrap(digitalConfirmation);
        String[] colorValues = new String[9];
        for (int i = 0; i < colorValues.length-1; i++) {
            byte[] bArray = new byte[32];
            int result = 0;
            bytebuf.get(bArray, 0, bArray.length);
            for(byte br : bArray) {
                result += br;
            }
            result = Math.abs(result);
            if(result > 255) result = result%255;
            colorValues[i] = Integer.toHexString(0x10 | result);
        }
        colorValues[8] = colorValues[0];
        return colorValues;
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry1 = LocateRegistry.getRegistry("localhost", 4444);
            // search for Registrar service
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry1.lookup("RegistrarService");
            // fire to localhost port 4445
            Registry myRegistry2 = LocateRegistry.getRegistry("localhost", 4445);
            // search for Mixing service
            mixImpl = (MixingInterface) myRegistry2.lookup("MixingService");

            if (regImpl.enrolUser(phone)) {
                System.out.println("User already enrolled or invalid phone number!");
                System.exit(1);
            }
            else {
                setEnrolStatus("Successfully enrolled!");
            }

            Runnable generateTokens = () -> {
                try {
                    int today = LocalDateTime.now().getMinute();
                    random = generateRandomValue();
                    dailyTokens = regImpl.getTokens(phone, today, random);
                    System.out.println("Daily tokens received");
                    if(!logValues.isEmpty()){
                        clearOldLogValues();
                    }
                } catch (RemoteException | NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            // iedere minuut(=dag)
            executor.scheduleAtFixedRate(generateTokens, 0, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserApp(String p){
        this.phone = p;
        this.dailyTokens = new ArrayList<>();
        this.logValues = new HashMap<>();
        this.tempLog = null;
        run();
    }

    public static void setEnrolStatus(String status) {enrollStatus = status;}

    public static String getEnrolStatus() {return enrollStatus;}

    public static String[] registerEntry(String qr) throws IOException, NoSuchAlgorithmException, SignatureException,
            InvalidKeyException {
        readQR(qr);
        Capsule capsule = new Capsule(tempLog.getDailyToken(), tempLog.getInterval(), tempLog.getHash(), random);
        byte[] digitalConfirmation = mixImpl.sendCapsule(capsule);
        boolean confirmed = confirmSignature(digitalConfirmation, capsule);
        if(!confirmed) return null;
        //System.out.println(Arrays.toString(digitalConfirmation));
        return getColor(digitalConfirmation);
    }

    public static String printLogs() {
        for(LocalDateTime key: logValues.keySet()){
            logValues.get(key).writeToFile();
        }
        return "Infection reported!";
    }

    public static String leaveFacility() {
        tempLog.setDeparture(LocalDateTime.now());
        return "Left facility at: " +tempLog.getDeparture().getHour()+":"+ tempLog.getDeparture().getMinute();
    }
}