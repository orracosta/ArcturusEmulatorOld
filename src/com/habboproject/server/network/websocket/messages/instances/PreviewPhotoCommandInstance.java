package com.habboproject.server.network.websocket.messages.instances;

import com.habboproject.server.utilities.JsonData;

/**
 * Created by brend on 28/02/2017.
 */
public class PreviewPhotoCommandInstance implements JsonData {
    private int playerId;
    private String fileName;

    public PreviewPhotoCommandInstance(int playerId, String fileName) {
        this.playerId = playerId;
        this.fileName = fileName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getFileName() {
        return fileName;
    }
}
