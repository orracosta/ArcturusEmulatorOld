package com.habboproject.server.network.messages.incoming.room.moderation;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.settings.RoomBannedListMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetBannedUsersMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) return;

        Room room = client.getPlayer().getEntity().getRoom();

        if ((room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        client.send(new RoomBannedListMessageComposer(room.getId(), room.getRights().getBannedPlayers()));
    }
}
