package com.habboproject.server.network.messages.incoming.room.moderation;

import com.habboproject.server.api.game.rooms.settings.RoomMuteState;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class MutePlayerMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int playerId = msg.readInt();
        int unk = msg.readInt();
        int lengthMinutes = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (client.getPlayer().getId() != room.getData().getOwnerId() && room.getData().getMuteState() != RoomMuteState.RIGHTS && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        if (playerId == room.getData().getOwnerId()) {
            return;
        }

        room.getRights().addMute(playerId, lengthMinutes);
    }
}
