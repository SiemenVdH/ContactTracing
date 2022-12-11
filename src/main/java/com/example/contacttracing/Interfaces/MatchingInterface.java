package com.example.contacttracing.Interfaces;

import com.example.contacttracing.Shared.Capsule;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public interface MatchingInterface extends Remote {
    void flushCapsules(ArrayList<Capsule> capsules) throws RemoteException;
}
