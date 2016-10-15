package com.shawasama.playsuit.media_class;

public class Album {

    private long id;
    private String title;
    private String artist;
    private int countSongs;
    private String artPath;

    public Album(long id, String title, String artist, int countSongs, String artPath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.countSongs = countSongs;
        this.artPath = artPath;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getCountSongs() {
        return countSongs;
    }

    public String getArtPath() {
        return artPath;
    }

    public long getID() {
        return id;
    }
}
