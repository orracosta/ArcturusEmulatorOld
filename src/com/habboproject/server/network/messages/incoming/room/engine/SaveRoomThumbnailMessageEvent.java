package com.habboproject.server.network.messages.incoming.room.engine;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.SaveRoomThumbnailMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.camera.CameraDao;

/**
 * Created by brend on 03/03/2017.
 */
public class SaveRoomThumbnailMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null || client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        Room room = client.getPlayer().getEntity().getRoom();

        if (!room.getRights().hasRights(client.getPlayer().getId()))
            return;

        int thumbId = CameraDao.saveThumbnail(room.getId());

        String thumbnail = "{0}-{1}";

        room.getData().setThumbnail(thumbnail.replace("{0}", "" + room.getId()).replace("{1}", "" + thumbId));

        client.send(new SaveRoomThumbnailMessageComposer());
    }
}
