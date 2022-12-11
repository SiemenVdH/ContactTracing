package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Shared.Capsule;
import com.example.contacttracing.Interfaces.MatchingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingInterface {
    private final MatchingService matchserv = new MatchingService();


    public MatchingServiceImpl() throws RemoteException {}

    @Override
    public void flushCapsules(ArrayList<Capsule> capsules) throws RemoteException {
        matchserv.addToCapsules(capsules);
    }
}
