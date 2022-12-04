package com.example.contacttracing.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;

public interface RegistrarInterface extends Remote {
    void deriveKeys(String CF) throws
            RemoteException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
    ArrayList<byte[]> getPseudoKeys() throws RemoteException, NoSuchAlgorithmException;
}
