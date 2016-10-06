package com.shawasama.playsuit.pojo;

/**
 * Created by Vlad on 07.10.2016.
 */

public class Util {
    public static String getSongDuration(long time) {
        String duration;
        long seconds = time/1000;
        long minutes = seconds/60;
        seconds %= 60;

        if(seconds<10){
            duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
        } else {
            duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
        }

        return duration;
    }
}
