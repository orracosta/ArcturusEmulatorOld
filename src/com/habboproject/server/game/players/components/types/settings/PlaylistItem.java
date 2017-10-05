package com.habboproject.server.game.players.components.types.settings;

import com.habboproject.server.api.game.players.data.types.IPlaylistItem;
import com.habboproject.server.utilities.JsonData;

public class PlaylistItem implements JsonData, IPlaylistItem {
    private String videoId;
    private String title;
    private String description;
    private int duration;

    public PlaylistItem(String videoId, String title, String description, int duration) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
