package com.shawasama.playsuit.pojo;

public class Album {

//    private long id;
    private String title;
    private String artist;
    private String countSongs;

    public Album(String title, String artist, String countSongs) {
        this.title = title;
        this.artist = artist;
        this.countSongs = countSongs;
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

    public String getCountSongs() {
        return countSongs;
    }

    public void setCountSongs(String countSongs) {
        this.countSongs = countSongs;
    }
}
