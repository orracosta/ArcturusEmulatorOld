package com.habboproject.server.network.messages.incoming.moderation;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ModToolRoomAlertMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int action = msg.readInt();

        String alert = msg.readString();
        String reason = msg.readString();

        if (!client.getPlayer().getPermissions().getRank().modTool()) {
            client.disconnect();
            return;
        }

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        Room room = client.getPlayer().getEntity().getRoom();

        room.getEntities().broadcastMessage(new AlertMessageComposer(alert));
        // TODO: Log these
    }
}
