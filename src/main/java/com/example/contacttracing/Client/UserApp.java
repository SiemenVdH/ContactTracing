package com.example.contacttracing.Client;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class UserApp {
    private String phone;

    public UserApp(){}

    private void run() {
        try {
            // fire to localhost port 4444
            Registry myRegistry = LocateRegistry.getRegistry("localhost", 4444);
            // search for SendService & ReceiveService
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry.lookup("RegistrarService");



        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void main(String[] args) {
        UserApp main = new UserApp();
        main.run();
    }
}