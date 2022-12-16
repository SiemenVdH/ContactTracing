package com.example.contacttracing.Client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Log {
    private String Ri;
    private String CF;
    private String hash;
    private LocalDateTime arrival;
    private LocalDateTime departure;
    private byte[] dailyToken;


    public Log(String ri, String cf, String h, LocalDateTime n, byte[] dT) {
        this.Ri = ri;
        this.CF = cf;
        this.hash = h;
        this.arrival = n;
        this.dailyToken = dT;
    }

    public String getHash() {return hash;}

    public byte[] getDailyToken() {return dailyToken;}

    public LocalDateTime getDeparture() {return departure;}

    public void setDeparture(LocalDateTime departure) {this.departure = departure;}

    public int[] getInterval() {
        int[] interval = new int[2];
        interval[0] = arrival.getHour();
        interval[1] = arrival.plusHours(1).getHour();
        return interval;
    }

    public void writeToFile() {
        try {
            File directory = new File("log.txt");
            FileWriter myWriter = new FileWriter(directory, true);
            myWriter.write(Ri+"/"+hash+"/"+Arrays.toString(getInterval())+"/"+Arrays.toString(dailyToken)+"\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
