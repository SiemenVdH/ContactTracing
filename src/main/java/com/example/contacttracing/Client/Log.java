package com.example.contacttracing.Client;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Log {
    private String Ri;
    private String CF;
    private String hash;
    private LocalDateTime now;
    private byte[] dailyToken;


    public Log(String ri, String cf, String h, LocalDateTime n, byte[] dT) {
        this.Ri = ri;
        this.CF = cf;
        this.hash = h;
        this.now = n;
        this.dailyToken = dT;
    }

    public String getHash() {return hash;}

    public byte[] getDailyToken() {return dailyToken;}

    public int[] getInterval() {
        int[] interval = new int[2];
        interval[0] = now.getHour();
        interval[1] = now.plusHours(1).getHour();
        return interval;
    }

    public void writeToFile() {
        try {
            FileWriter myWriter = new FileWriter("C:\\Users\\renau\\IdeaProjects\\ContactTracing\\log.txt");
            myWriter.write(Ri+"/"+hash+"/"+ Arrays.toString(dailyToken) +"/"+ Arrays.toString(getInterval()));
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
