package com.shawasama.playsuit.pojo;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCountSongs() {
        return countSongs;
    }

    public void setCountSongs(int countSongs) {
        this.countSongs = countSongs;
    }
}
