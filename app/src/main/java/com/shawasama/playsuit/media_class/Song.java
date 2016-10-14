package com.shawasama.playsuit.media_class;


public class Song {
    private long id;
    private String artist;
    private String title;
    private long duration;
    private String path;
    private String fileName;
    private long albumID;
    private String albumName;

    public Song(
            long id,
            String artist,
            String title,
            long duration,
            String path,
            String fileName,
            long albumID, String albumName) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.path = path;
        this.fileName = fileName;
        this.albumID = albumID;
        this.albumName = albumName;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
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

    public String getAlbumName() {
        return albumName;
    }
}
