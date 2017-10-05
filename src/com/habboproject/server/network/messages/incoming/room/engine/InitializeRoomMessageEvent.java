package com.habboproject.server.network.messages.incoming.room.engine;

import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class InitializeRoomMessageEvent implements Event {
    // Temporary hacky shizzle for room loading, will keep using for now, I will remove if any issues
    public static final LoadHeightmapMessageEvent heightmapMessageEvent = new LoadHeightmapMessageEvent();
//    public static final AddUserToRoomMessageEvent addUserToRoomMessageEvent = new AddUserToRoomMessageEvent();

    public void handle(Session client, MessageEvent msg) {
        int id = msg.readInt();
        String password = msg.readString();

        if(client.getPlayer() == null) {
            return;
        }

        if(System.currentTimeMillis() - client.getPlayer().getLastRoomRequest() < 500) {
            return;
        }

        if(client.getPlayer().getEntity() != null && !client.getPlayer().isSpectating(id) && !client.getPlayer().hasQueued(id)) {
            if(!client.getPlayer().getEntity().isFinalized()) {
                client.getPlayer().setEntity(null);
            }
        }

        client.getPlayer().setLastRoomRequest(System.currentTimeMillis());

        RoomManager.getInstance().initializeRoom(client, id, password);
    }
}
