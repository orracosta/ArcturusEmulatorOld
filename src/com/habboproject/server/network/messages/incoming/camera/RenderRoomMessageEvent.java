package com.habboproject.server.network.messages.incoming.camera;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.camera.PhotoPreviewMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.camera.CameraDao;

/**
 * Created by brend on 03/03/2017.
 */
public class RenderRoomMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null || client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        int photoId = CameraDao.savePhoto(client.getPlayer().getId());

        client.getPlayer().getEntity().setLastPhoto(photoId);

        String fileUrl = "{0}-{1}.png";

        client.send(new PhotoPreviewMessageComposer(fileUrl.replace("{0}", "" + client.getPlayer().getId()).replace("{1}", "" + photoId)));
    }
}
