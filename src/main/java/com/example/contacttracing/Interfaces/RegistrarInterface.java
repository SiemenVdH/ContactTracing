package com.example.contacttracing.Interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.crypto.*;
import java.security.*;
import java.util.*;

public interface RegistrarInterface extends Remote {
    void deriveKeys(String CF) throws
            RemoteException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException;
    ArrayList<byte[]> getPseudoKeys(String CF) throws RemoteException, NoSuchAlgorithmException;

    void clearDailyAndPseudoKeys(String CF) throws RemoteException;
}
