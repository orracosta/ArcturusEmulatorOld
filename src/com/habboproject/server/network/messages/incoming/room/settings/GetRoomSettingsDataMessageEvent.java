package com.habboproject.server.network.messages.incoming.room.settings;

import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.settings.RoomSettingsDataMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetRoomSettingsDataMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int roomId = msg.readInt();

        Room room = RoomManager.getInstance().get(roomId);

        if (room == null) {
            return;
        }

        if (room.getData().getOwnerId() == client.getPlayer().getId() || client.getPlayer().getPermissions().getRank().roomFullControl()) {
            client.send(new RoomSettingsDataMessageComposer(room, client.getPlayer().getPermissions().getRank().modTool()));
        }
    }
}
