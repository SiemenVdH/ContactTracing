package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Interfaces.MatchingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingInterface {
    MatchingService mixprox = new MatchingService();

    protected MatchingServiceImpl() throws RemoteException {}

}
