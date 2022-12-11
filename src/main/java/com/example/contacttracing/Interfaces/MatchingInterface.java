package com.example.contacttracing.Interfaces;

import com.example.contacttracing.Shared.Capsule;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.security.PublicKey;
import java.util.ArrayList;

public interface MatchingInterface extends Remote {
    void flushCapsules(ArrayList<Capsule> capsules) throws RemoteException;
    void forwardLogs(PublicKey publicKey) throws RemoteException;
}
