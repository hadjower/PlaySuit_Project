package com.shawasama.playsuit.pojo;


public class Song {
    private long id;
    private String artist;
    private String title;
    private long duration;
    private String path;
    private String fileName;
    private long albumID;

    public Song(
            long id,
            String artist,
            String title,
            long duration,
            String path,
            String fileName,
            long albumID) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.path = path;
        this.fileName = fileName;
        this.albumID = albumID;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public long getAlbumID() {
        return albumID;
    }
}
