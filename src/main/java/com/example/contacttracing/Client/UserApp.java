package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.MixingInterface;
import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.time.*;
import java.util.*;

public class UserApp {
    private String phone;
    private ArrayList<byte[]> dailyTokens;
    private Map<LocalDateTime, Log> logValues;


    private Log readQR(String qrText) {
        String[] extracted = qrText.split("@");
        Log log = new Log(extracted[0], extracted[1], extracted[2], LocalDateTime.now());
        logValues.put(LocalDateTime.now(), log);
        return log;
    }

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry1 = LocateRegistry.getRegistry("localhost", 4444);
            // search for Registrar service
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry1.lookup("RegistrarService");
            // fire to localhost port 5555
            Registry myRegistry2 = LocateRegistry.getRegistry("localhost", 5555);
            // search for Mixing service
            MixingInterface mixImpl = (MixingInterface) myRegistry2.lookup("MixingService");

            int today = LocalDateTime.now().getDayOfMonth();
            boolean firstDay = true;

            if(regImpl.enrolUser(phone)) {
                System.out.println("User is already enrolt!");
            }
            else {
                System.out.println("Succesfully enrolt!");
                while(true) {
                    if(firstDay || LocalDateTime.now().getDayOfMonth()!=today) {
                        regImpl.clearDailyTokens(phone);
                        firstDay = false;
                        today = LocalDateTime.now().getDayOfMonth();
                        dailyTokens = regImpl.getTokens(phone, today);
                    }
                    Log log = readQR("");
                    Capsule capsule = new Capsule(dailyTokens.remove(0), log.getIneterval(), log.getHash());
                    mixImpl.sendCapsule(capsule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserApp(String p){
        this.phone = p;
        this.dailyTokens = new ArrayList<>();
        this.logValues = new HashMap<>();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Phone: ");
        String phone = input.nextLine();

        UserApp main = new UserApp(phone);
        main.run();
    }
}