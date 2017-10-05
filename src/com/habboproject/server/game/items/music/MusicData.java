package com.habboproject.server.game.items.music;

public class MusicData {
    private int songId;
    private String name;

    private String title;
    private String artist;
    private String data;

    private int lengthSeconds;

    public MusicData(int songId, String name, String title, String artist, String data, int lengthSeconds) {
        this.songId = songId;
        this.name = name;
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.lengthSeconds = lengthSeconds;
    }

    public int getSongId() {
        return songId;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getData() {
        return data;
    }

    public int getLengthSeconds() {
        return this.lengthSeconds;
    }

    public int getLengthMilliseconds() {
        return lengthSeconds * 1000;
    }
}
