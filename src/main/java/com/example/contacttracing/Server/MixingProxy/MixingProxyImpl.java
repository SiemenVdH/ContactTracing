package com.example.contacttracing.Server.MixingProxy;

import com.example.contacttracing.Client.Capsule;
import com.example.contacttracing.Interfaces.MixingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDateTime;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingInterface {
    MixingProxy mixprox = new MixingProxy();

    protected MixingProxyImpl() throws RemoteException {}

    private PublicKey getPublicKey() {
        return mixprox.getPublicKey();
    }

    @Override
    public void sendCapsule(Capsule capsule) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        byte day = (byte) LocalDateTime.now().getDayOfMonth();


        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(getPublicKey());
        //signature.update();
        boolean isCorrect = signature.verify(capsule.getDailyToken());
    }
}
