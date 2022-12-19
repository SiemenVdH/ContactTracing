package com.example.contacttracing.Server.MatchingService;

import com.example.contacttracing.Shared.Capsule;
import com.example.contacttracing.Interfaces.MatchingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.security.*;
import java.util.ArrayList;

public class MatchingServiceImpl extends UnicastRemoteObject implements MatchingInterface {
    private final MatchingService matchserv;

    private boolean confirmSignature(PublicKey publicKey, byte[] digitalSignature, String data) throws
            NoSuchAlgorithmException, InvalidKeyException, SignatureException
    {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        return signature.verify(digitalSignature);
    }

    public MatchingServiceImpl(MatchingService ms) throws RemoteException {this.matchserv = ms;}

    @Override
    public void flushCapsules(ArrayList<Capsule> capsules) throws RemoteException {
        matchserv.addToCapsules(capsules);
    }

    @Override
    public boolean forwardLogs(PublicKey publicKey, byte[] digitalSignature, String logData) throws RemoteException,
            NoSuchAlgorithmException, SignatureException, InvalidKeyException
    {
        boolean confirmed = confirmSignature(publicKey, digitalSignature, logData);
        if(confirmed) {
            matchserv.addLogs(logData);
            return true;
        }
        return false;
    }
}
