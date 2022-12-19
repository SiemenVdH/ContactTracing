package com.example.contacttracing.Server.MixingProxy;

import com.example.contacttracing.Shared.Capsule;
import com.example.contacttracing.Interfaces.MixingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.security.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingInterface {
    private MixingProxy mixprox;
    private ArrayList<byte[]> usedTokens;

    private PublicKey getPublicKeyRegistrar() {
        return mixprox.getPublicKeyRegistrar();
    }

    private boolean verifySignature(byte[] dailyToken, byte[] random) throws InvalidKeyException, NoSuchAlgorithmException,
            SignatureException
    {
        byte day = (byte) LocalDateTime.now().getMinute();
        byte[] combined = new byte[random.length + day];
        for (int j = 0; j < combined.length; j++) {
            combined[j] = j < random.length ? random[j] : day;
        }

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(getPublicKeyRegistrar());
        signature.update(combined);
        return signature.verify(dailyToken);
    }

    private byte[] signHash(String hash) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(mixprox.getPrivateKey());
        signature.update(hash.getBytes());
        return signature.sign();
    }

    public MixingProxyImpl(MixingProxy mp) throws RemoteException {
        this.mixprox = mp;
        this.usedTokens = new ArrayList<>();
    }

    @Override
    public byte[] sendCapsule(Capsule capsule) throws RemoteException, NoSuchAlgorithmException, InvalidKeyException,
            SignatureException
    {
        mixprox.storeCapsule(LocalDateTime.now(), capsule);
        if(verifySignature(capsule.getDailyToken(), capsule.getRandom()) &&
                !usedTokens.contains(capsule.getDailyToken())) {
            usedTokens.add(capsule.getDailyToken());
            return signHash(capsule.getHash());
        }
        else {
            System.out.println("Invalid token received");
            return new byte[256];
        }
    }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return mixprox.getPublicKey();
    }
}
