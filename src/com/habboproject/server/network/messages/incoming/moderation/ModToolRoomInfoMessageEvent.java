package com.habboproject.server.network.messages.incoming.moderation;

import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.moderation.ModToolRoomInfoMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ModToolRoomInfoMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int roomId = msg.readInt();

        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            // fuck off
            client.getLogger().error(
                    ModToolUserInfoMessageEvent.class.getName() + " - tried to view room info for room: " + roomId);
            client.disconnect();
            return;
        }

        Room room = RoomManager.getInstance().get(roomId);

        if (room == null)
            return;

        client.send(new ModToolRoomInfoMessageComposer(room));
    }
}
