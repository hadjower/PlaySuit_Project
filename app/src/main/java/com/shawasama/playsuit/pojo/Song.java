package com.shawasama.playsuit.pojo;


public class Song {
    private long id;
    private String artist;
    private String title;
    private long duration;

    public Song(long id, String artist, String title, long duration) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
