package com.shawasama.playsuit.util;


import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

public class Util {
    public static String getTimeInText(long time) {
        StringBuilder duration = new StringBuilder("");
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds %= 60;
        minutes %= 60;

        if (hours > 0) {
            duration.append(String.valueOf(hours)).append(":");
            if (minutes < 10)
                duration.append("0");
        }

        if (seconds < 10) {
            duration.append(String.valueOf(minutes)).append(":0").append(String.valueOf(seconds));
        } else {
            duration.append(String.valueOf(minutes)).append(":").append(String.valueOf(seconds));
        }

        return duration.toString();
    }

    public static boolean isAudio(File file) {
        String path = file.getAbsolutePath();
        String fileExtension = "";
        int dotPos = path.lastIndexOf('.');
        if (0 <= dotPos) {
            fileExtension = path.substring(dotPos + 1);
        }

        if (!TextUtils.isEmpty(fileExtension)) {
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
            if (!TextUtils.isEmpty(mime)) {
                return mime.startsWith("audio");
            }
        }
        return false;
    }

    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    public static String getTimeInText(long dur, int progress) {
        long currentDur = (dur * progress) / 100;
        return getTimeInText(currentDur);
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
