package com.example.contacttracing.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.security.*;
import java.util.*;

public interface RegistrarInterface extends Remote {
    Map<Integer,byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException;
    boolean enrolUser(String phone) throws RemoteException;
    ArrayList<byte[]> getTokens(String phone, int today, byte[] random) throws RemoteException, NoSuchAlgorithmException,
            SignatureException, InvalidKeyException;
    PublicKey getPublicKey() throws RemoteException;
    Map<String, byte[]> getPseudosFromDay(int day) throws RemoteException;
}
