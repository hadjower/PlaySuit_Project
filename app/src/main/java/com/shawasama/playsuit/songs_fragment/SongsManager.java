package com.shawasama.playsuit.songs_fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;

import com.shawasama.playsuit.R;
import com.shawasama.playsuit.media_class.Song;
import com.shawasama.playsuit.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SongsManager {
    private static SongsManager instance;

    private List<Song> songList;
    private PathSongInfoContainer container;
    private Map<String, Song> songMap;

    private String rootDir;


    private SongsManager() {
    }

    public static synchronized SongsManager getInstance() {
        if (instance == null)
            instance = new SongsManager();
        return instance;
    }

    public void setSongList(Context context) {
        if (songList != null) {
            return;
        }
        songList = new ArrayList<>();
        songMap = new HashMap<>();
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
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.ALBUM
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
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            List<String> songDirPaths = new ArrayList<>();
            //add songs to list
            do {
                if (musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)) == 0) {
                    continue;
                }

                long thisID = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisDuration = musicCursor.getLong(durationColumn);
                String thisPath = musicCursor.getString(dataColumn);
                String thisFileName = musicCursor.getString(fileNameColumn);
                long thisAlbumID = musicCursor.getLong(albumIDColumn);
                String thisAlbum = musicCursor.getString(albumColumn);

                Song thisSong = new Song(
                        thisID,
                        thisArtist,
                        thisTitle,
                        thisDuration,
                        thisPath,
                        thisFileName,
                        thisAlbumID,
                        thisAlbum
                );
                songList.add(thisSong);
                songMap.put(thisPath, thisSong);

                thisPath = thisPath.substring(0, thisPath.lastIndexOf("/"));

                if (rootDir == null) {
                    rootDir = "/";
                    rootDir += thisPath.split("/")[1];
                }

                if (!songDirPaths.contains(thisPath)) {
                    songDirPaths.add(thisPath);
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
        if (container == null) {
            Log.e("SongsManager", " Field container is null, while trying to call method isStoresMusic");
            return false;
        }
        return container.isStoresMusic(directoryPath);
    }

    public SparseArray<Long> getFolderInfo(File directory) {
        SparseArray<Long> folderInfo = new SparseArray<>(2);
        long[] amAndDur = container.getFolderInfoByPath(directory.getAbsolutePath());
        if (amAndDur != null) {
            folderInfo.put(Constants.AMOUNT_KEY, amAndDur[Constants.AMOUNT_KEY]);
            folderInfo.put(Constants.DURATION_KEY, amAndDur[Constants.DURATION_KEY]);
            return folderInfo;
        }
        return null;
    }

    public Song getSong(String path) {
        return songMap.get(path);
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
                if (info.path.contains(directoryPath)) {
                    return true;
                }
            }
            return false;
        }

        long[] getFolderInfoByPath(String path) {
            long[] amAndDur = new long[2];
            int amountIndex = Constants.AMOUNT_KEY;
            int durIndex = Constants.DURATION_KEY;
            amAndDur[amountIndex] = amAndDur[durIndex] = 0;
            for (PathSongsInfo info : infoList) {
                if (info.path.contains(path)) {
                    amAndDur[amountIndex] += info.amountOfSongs;
                    amAndDur[durIndex] += info.sumDuration;
                }
            }
            return amAndDur;
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

