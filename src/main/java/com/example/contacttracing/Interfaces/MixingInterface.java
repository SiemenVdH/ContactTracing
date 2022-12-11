package com.example.contacttracing.Interfaces;

import com.example.contacttracing.Shared.Capsule;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.security.*;

public interface MixingInterface extends Remote {
    byte[] sendCapsule(Capsule capsule) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException;

    PublicKey getPublicKey() throws RemoteException;
}
