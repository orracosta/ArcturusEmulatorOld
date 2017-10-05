package com.habboproject.server.network.messages.incoming.room.engine;

import com.habboproject.server.api.game.rooms.settings.RoomAccessType;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.FollowRoomDataMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class FollowRoomInfoMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int roomId = msg.readInt();
        boolean isLoading = msg.readInt() == 1;
        boolean checkEntry = msg.readInt() == 1;

        Room room = RoomManager.getInstance().get(roomId);

        if (room == null || room.getData() == null) {
            return;
        }

        boolean skipAuth = false;

        if(room.getData().getAccess() != RoomAccessType.OPEN) {
            if (room.getRights().hasRights(client.getPlayer().getId())) {
                skipAuth = true;
            } else if (client.getPlayer().isTeleporting() || client.getPlayer().isBypassingRoomAuth()) {
                skipAuth = true;
            }
        }

        client.send(new FollowRoomDataMessageComposer(room.getData(), isLoading, checkEntry, skipAuth, room.getRights().hasRights(client.getPlayer().getId()) || client.getPlayer().getPermissions().getRank().roomFullControl()));
    }
}
