package com.example.contacttracing.Client;
import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CateringFacility {
    private String name;
    private String city;
    private String phone;
    private String bN; // Business number

    public CateringFacility(String n,String c,String p, String busNum){
        this.name = n;
        this.city = c;
        this.phone = p;
        this.bN = busNum;
    }

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
        CateringFacility main = new CateringFacility("Dominos", "Gent", "0466778899", "112233445566");
        main.run();
    }
}
