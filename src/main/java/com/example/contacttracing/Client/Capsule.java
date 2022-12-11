package com.example.contacttracing.Client;

public class Capsule {
    private int[] interval;
    private String hash;
    private byte[] dailyToken;

    public Capsule(byte[] dT, int[] i, String h) {
        this.dailyToken = dT;
        this.interval = i;
        this.hash = h;
    }

    public int[] getInterval() {
        return interval;
    }

    public String getHash() {
        return hash;
    }

    public byte[] getDailyToken() {
        return dailyToken;
    }
}
