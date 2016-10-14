package com.shawasama.playsuit.folders_fragment;

import com.shawasama.playsuit.songs_fragment.SongsManager;
import com.shawasama.playsuit.util.Constants;
import com.shawasama.playsuit.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FoldersManager {
    private static FoldersManager instance;

    private String rootDir;

    private FoldersManager() {
    }

    public static synchronized FoldersManager getInstance() {
        if (instance == null)
            instance = new FoldersManager();
        return instance;
    }

    public String getRootDir() {
        if (rootDir == null)
            rootDir = SongsManager.getInstance().getRootDir();
        return rootDir;
    }

    public List<File> getFoldersAndMusic(String dirPath){
        List<File> foldersAndMusic = new ArrayList<>();

        String currentDir = FoldersFragment.getCurrentDir();

        File f = new File(dirPath);
        List<File> files = Arrays.asList(f.listFiles());
        File emptyFile;

        if (!currentDir.equals(rootDir)) {
            //add empty file so we could go back by clicking its view
            emptyFile = new File("..");
            foldersAndMusic.add(emptyFile);
        }

        if (!files.isEmpty()) {
            //Sort the files by name.
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return f1.getName().compareTo(f2.getName());
                }
            });

            //Sort the files by type(folder or music)
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    int typeF1 = f1.isDirectory() ? Constants.FOLDER : Constants.AUDIO;
                    int typeF2 = f2.isDirectory() ? Constants.FOLDER : Constants.AUDIO;
                    return typeF1 - typeF2;
                }
            });

            for (File file : files) {
                if (file != null && !file.isHidden() && file.canRead()) {

                    if (file.isDirectory()) {
                        String filePath = file.getAbsolutePath();
                        if (SongsManager.getInstance().isStoresMusic(filePath)) {
                            foldersAndMusic.add(file);
                        }

                    } else if (Util.isAudio(file)) {
                        foldersAndMusic.add(file);
                    }

                }

            }
        }
        return foldersAndMusic;
    }
}
