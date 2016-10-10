package com.shawasama.playsuit.pojo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.shawasama.playsuit.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class AudioContainer {
    private static AudioContainer instance;

    private List<Song> songList;
    private List<String> songPaths;
    private PathSongInfoContainer container;
    private String rootDir;


    private AudioContainer() {
    }

    public static synchronized AudioContainer getInstance() {
        if (instance == null)
            instance = new AudioContainer();
        return instance;
    }

    public void setSongList(Context context) {
        if (songList != null) {
            return;
        }
        songList = new ArrayList<>();
        container = new PathSongInfoContainer();

        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor musicCursor = musicResolver.query(musicUri, projection, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int durationColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int dataColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int fileNameColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int albumIDColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);

            songPaths = new ArrayList<>();

            //add songs to list
            do {
                long thisID = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                String thisPath = musicCursor.getString(dataColumn);
                String thisFileName = musicCursor.getString(fileNameColumn);
                long thisAlbumID = musicCursor.getLong(albumIDColumn);
                songList.add(new Song(
                        thisID,
                        thisArtist,
                        thisTitle,
                        thisDuration,
                        thisPath,
                        thisFileName,
                        thisAlbumID
                ));

                thisPath = thisPath.substring(0, thisPath.lastIndexOf("/"));
                if (rootDir == null) {
                    rootDir = "/";
                    rootDir += thisPath.split("/")[1];
                }

                if (!songPaths.contains(thisPath)) {
                    songPaths.add(thisPath);
                    container.put(thisPath, thisDuration);
                } else {
                    PathSongsInfo infoInstance = container.get(thisPath);
                    if (infoInstance != null) {
                        infoInstance.add(thisDuration);
                    } else {
                        Log.e(context.getString(R.string.path_song_info), "Error while try to add info from " + thisPath);
                    }
                }

            } while (musicCursor.moveToNext());
            musicCursor.close();
        }

        Collections.sort(songList, new Comparator<Song>() {
            @Override
            public int compare(Song s1, Song s2) {
                String song2Title = s2.getTitle();
                String song1Title = s1.getTitle();
                return song1Title.compareTo(song2Title);
            }
        });
    }

    public List<Song> getSongList() {
        return songList;
    }

    public String getRootDir() {
        return rootDir;
    }

    public boolean isStoresMusic(String directoryPath) {
        if (container == null)
            return false;

        return container.isStoresMusic(directoryPath);
    }

    private class PathSongInfoContainer {
        List<PathSongsInfo> infoList;

        PathSongInfoContainer() {
            infoList = new ArrayList<>();
        }

        void put(String thisPath, long thisDuration) {
            infoList.add(new PathSongsInfo(thisPath, 1, thisDuration));
        }

        PathSongsInfo get(String thisPath) {
            for (PathSongsInfo info : infoList) {
                if (info.path.equals(thisPath)) {
                    return info;
                }
            }
            return null;
        }

        boolean isStoresMusic(String directoryPath) {
            for (PathSongsInfo info : infoList) {
                if (info.path.contains(directoryPath))
                    return true;
            }
            return false;
        }
    }

    private class PathSongsInfo {
        String path;
        int amountOfSongs;
        long sumDuration;

        PathSongsInfo(String path, int amountOfSongs, long sumDuration) {
            this.path = path;
            this.amountOfSongs = amountOfSongs;
            this.sumDuration = sumDuration;
        }

        void add(long thisDuration) {
            amountOfSongs++;
            sumDuration += thisDuration;
        }
    }
}
