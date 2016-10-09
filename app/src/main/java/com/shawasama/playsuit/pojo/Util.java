package com.shawasama.playsuit.pojo;


import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

public class Util {
    public static String getSongDuration(long time) {
        String duration;
        long seconds = time / 1000;
        long minutes = seconds / 60;
        seconds %= 60;

        if (seconds < 10) {
            duration = String.valueOf(minutes) + ":0" + String.valueOf(seconds);
        } else {
            duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
        }

        return duration;
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
}
