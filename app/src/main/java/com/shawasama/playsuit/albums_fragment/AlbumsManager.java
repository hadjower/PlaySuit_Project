package com.shawasama.playsuit.albums_fragment;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.shawasama.playsuit.media_class.Album;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumsManager {
    private static AlbumsManager instance;

    List<Album> albums;

    private AlbumsManager() {
    }

    public static synchronized AlbumsManager getInstance() {
        if (instance == null)
            instance = new AlbumsManager();
        return instance;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void loadAlbums(Context context) {
        ContentResolver musicResolver = context.getContentResolver();
        Uri musicUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS
        };
        Cursor albumCursor = musicResolver.query(musicUri, projection, null, null, null);

        if (albumCursor != null && albumCursor.moveToFirst()) {
            albums = new ArrayList<>();

            int idCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int albumCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artistCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int artCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int songsCol = albumCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);

            do {
                long id = albumCursor.getLong(idCol);
                String name = albumCursor.getString(albumCol);
                String artist = albumCursor.getString(artistCol);
                String artPath = albumCursor.getString(artCol);
                int songs = albumCursor.getInt(songsCol);
                albums.add(new Album(id, name, artist, songs, artPath));
            } while (albumCursor.moveToNext());
            albumCursor.close();

            Collections.sort(albums, new Comparator<Album>() {
                @Override
                public int compare(Album a1, Album a2) {
                    return a1.getTitle().compareTo(a2.getTitle());
                }
            });
        }


    }
}
