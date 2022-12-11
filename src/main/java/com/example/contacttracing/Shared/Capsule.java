package com.example.contacttracing.Shared;

public class Capsule {
    private int[] interval;
    private String hash;
    private byte[] dailyToken;
    private byte[] random;

    public Capsule(byte[] dT, int[] i, String h, byte[] r) {
        this.dailyToken = dT;
        this.interval = i;
        this.hash = h;
        this.random = r;
    }

    public int[] getInterval() {
        return interval;
    }
    public String getHash() {return hash;}
    public byte[] getDailyToken() {
        return dailyToken;
    }
    public byte[] getRandom() {return random;}
}
