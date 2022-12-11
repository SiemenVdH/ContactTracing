package com.example.contacttracing.Client;

import java.time.LocalDateTime;

public class Log {
    private String Ri;
    private String CF;
    private String hash;
    private LocalDateTime now;


    public Log(String ri, String cf, String h, LocalDateTime n) {
        this.Ri = ri;
        this.CF = cf;
        this.hash = h;
        this.now = n;
    }

    public String getHash() {return hash;}

    public int[] getIneterval() {
        int[] interval = new int[2];
        interval[0] = now.getHour();
        interval[1] = now.plusHours(1).getHour();
        return interval;
    }
}
