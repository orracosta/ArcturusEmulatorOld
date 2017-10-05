package com.habboproject.server.api.game.players.data.types;

public interface IPlaylistItem {
    String getVideoId();

    void setVideoId(String videoId);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    int getDuration();

    void setDuration(int duration);
}
