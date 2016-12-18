package com.shawasama.playsuit.media_class;


public class Artist {
    private long id;
    private String name;
    private int albumsCount;
    private int songCount;

    public Artist(long id, String name, int albumsCount, int songCount) {
        this.id = id;
        this.name = name;
        this.albumsCount = albumsCount;
        this.songCount = songCount;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAlbumsCount() {
        return albumsCount;
    }

    public int getSongCount() {
        return songCount;
    }
}
