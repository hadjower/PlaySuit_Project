package com.shawasama.playsuit.artists_fragment;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.shawasama.playsuit.db_helper.MediaDBHelper;
import com.shawasama.playsuit.media_class.Artist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArtistsManager {
    private static ArtistsManager instance;
//    private List<Artist> artists;

    private ArtistsManager(){
    }

    public static synchronized ArtistsManager getInstance() {
        if (instance == null)
            instance = new ArtistsManager();
        return instance;
    }

//    public List<Artist> getArtists() {
//        return artists;
//    }

    public List<Artist> loadArtists(Context context) {
        List<Artist> artists = new ArrayList<>();
        Cursor artistsCursor = MediaDBHelper.getAllArtists(context);

        try {
            if (artistsCursor!= null && artistsCursor.moveToFirst()) {
                int idCol = artistsCursor.getColumnIndex(MediaStore.Audio.Artists._ID);
                int nameCol = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
                int albumsCount = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
                int songCountCol = artistsCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

                do {
                    long id = artistsCursor.getLong(idCol);
                    String name = artistsCursor.getString(nameCol);
                    int songCount = artistsCursor.getInt(songCountCol);
                    artists.add(new Artist(id, name, albumsCount, songCount));
                } while (artistsCursor.moveToNext());
            }
        } finally {
            if (artistsCursor != null)
                artistsCursor.close();
        }
        if (artists== null)
            return null;
        Collections.sort(artists, new Comparator<Artist>() {
            @Override
            public int compare(Artist a1, Artist a2) {
                return a1.getName().compareTo(a2.getName());
            }
        });
        return artists;
    }
}
