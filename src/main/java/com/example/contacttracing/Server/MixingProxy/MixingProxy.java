package com.example.contacttracing.Server.MixingProxy;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;

public class MixingProxy {
    private PublicKey publicKey;

    private void startServer() {
        try {
            // create on port 5555
            Registry registry = LocateRegistry.createRegistry(5555);
            // create new services
            registry.rebind("MixingService", new MixingProxyImpl());

            // fire to localhost port 4444
            Registry myRegistry1 = LocateRegistry.getRegistry("localhost", 4444);
            // search for Registrar service
            RegistrarInterface regImpl = (RegistrarInterface) myRegistry1.lookup("RegistrarService");

            publicKey = regImpl.getPublicKey();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("System is ready");
    }

    public PublicKey getPublicKey() {return publicKey;}

    public static void main(String[] args) {
        MixingProxy main = new MixingProxy();
        main.startServer();
    }
}
