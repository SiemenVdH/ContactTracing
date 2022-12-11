package com.example.contacttracing.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.security.*;
import java.util.*;

public interface RegistrarInterface extends Remote {
    void deriveKeys(String CF) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException;
    ArrayList<byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException;
    boolean enrolUser(String phone) throws RemoteException;
    ArrayList<byte[]> getTokens(String phone, int today, byte[] random) throws RemoteException, NoSuchAlgorithmException,
            SignatureException, InvalidKeyException;
    PublicKey getPublicKey() throws RemoteException;
    Map<String, ArrayList<byte[]>> getAllPseudos() throws RemoteException;
}
