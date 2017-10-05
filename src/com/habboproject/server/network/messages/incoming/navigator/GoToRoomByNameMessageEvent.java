package com.habboproject.server.network.messages.incoming.navigator;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 31/01/2017.
 */
public class GoToRoomByNameMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String findingMode = msg.readString();
        int roomId = 0;

        switch (findingMode) {
            case "predefined_noob_lobby":
                roomId = Comet.getServer().getConfig().getInt("comet.game.predefined.noob.lobby");
                break;
            case "random_friending_room":
                roomId = RoomManager.getInstance().getRandomLoadedRoom();
                break;
        }

        if (roomId != 0) {
            client.send(new RoomForwardMessageComposer(roomId));
        }
    }
}