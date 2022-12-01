package com.example.contacttracing.Server.MixingProxy;

import com.example.contacttracing.Interfaces.MixingInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MixingProxyImpl extends UnicastRemoteObject implements MixingInterface {
    MixingProxy mixprox = new MixingProxy();

    protected MixingProxyImpl() throws RemoteException {}

}
