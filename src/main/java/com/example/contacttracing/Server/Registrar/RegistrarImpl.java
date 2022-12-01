package com.example.contacttracing.Server.Registrar;

import com.example.contacttracing.Interfaces.RegistrarInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegistrarImpl extends UnicastRemoteObject implements RegistrarInterface {
    Registrar reg = new Registrar();
    protected RegistrarImpl() throws RemoteException {}

}
