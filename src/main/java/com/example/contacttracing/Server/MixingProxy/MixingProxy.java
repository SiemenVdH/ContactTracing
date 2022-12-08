package com.example.contacttracing.Server.MixingProxy;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MixingProxy {

    private void startServer() {
        try {
            // create on port 4444
            Registry registry = LocateRegistry.createRegistry(5555);
            // create new services
            registry.rebind("MixingService", new MixingProxyImpl());


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("System is ready");
    }

    public static void main(String[] args) {
        MixingProxy main = new MixingProxy();
        main.startServer();
    }
}
