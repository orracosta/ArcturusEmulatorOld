package com.habboproject.server.network.websocket.messages.executors;

import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.camera.PhotoPreviewMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.network.websocket.messages.WebSocketCommandExecutor;
import com.habboproject.server.network.websocket.messages.instances.PreviewPhotoCommandInstance;

/**
 * Created by brend on 28/02/2017.
 */
public class PreviewPhotoCommandExecutor extends WebSocketCommandExecutor<PreviewPhotoCommandInstance> {
    public PreviewPhotoCommandExecutor() {
        super(PreviewPhotoCommandInstance.class);
    }

    @Override
    public void handle(PreviewPhotoCommandInstance instance) {
        Session client = NetworkManager.getInstance().getSessions().getByPlayerId(instance.getPlayerId());
        if (client == null || client.getPlayer() == null) {
            return;
        }

        if (client != null && client.getPlayer() != null) {
            client.send(new PhotoPreviewMessageComposer(instance.getFileName() + ".png"));
        }
    }
}
