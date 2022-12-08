package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class UserApp {
    private String phone;
    private ArrayList<byte[]> dailyTokens;


    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

            int today = LocalDateTime.now().getDayOfMonth();
            boolean firstDay = true; // generate QR on start day

            if(regImpl.enrolUser(phone)) {
                System.out.println("User is already enrolt!");
            }
            else {
                System.out.println("Succesfully enrolt!");
                while(true) {
                    if(firstDay || LocalDateTime.now().getDayOfMonth()!=today) {
                        firstDay = false;
                        today = LocalDateTime.now().getDayOfMonth();
                        dailyTokens = regImpl.getTokens(phone, today);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserApp(String p){
        this.phone = p;
        this.dailyTokens = new ArrayList<>();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Phone: ");
        String phone = input.nextLine();

        UserApp main = new UserApp(phone);
        main.run();
    }
}