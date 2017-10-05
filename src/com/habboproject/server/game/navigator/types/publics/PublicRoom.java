package com.habboproject.server.game.navigator.types.publics;

public class PublicRoom {
    private final int roomId;
    private final String caption;
    private final String description;
    private final String imageUrl;

    public PublicRoom(int roomId, String caption, String description, String imageUrl) {
        this.roomId = roomId;
        this.caption = caption;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
