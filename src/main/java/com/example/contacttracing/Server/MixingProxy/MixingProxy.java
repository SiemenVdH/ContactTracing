package com.example.contacttracing.Server.MixingProxy;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;

public class MixingProxy {

    private void startServer() {
        try {
            // create on port 4444
            Registry registry = LocateRegistry.createRegistry(6666);
            // create new services
            registry.rebind("MixingService", new MixingProxyImpl());


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("System is ready");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MixingProxy main = new MixingProxy();
        main.startServer();
    }
}
