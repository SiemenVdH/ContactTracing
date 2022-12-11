package com.example.contacttracing.Interfaces;

import com.example.contacttracing.Client.Capsule;

import java.rmi.Remote;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public interface MixingInterface extends Remote {
    void sendCapsule(Capsule capsule) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;
}
