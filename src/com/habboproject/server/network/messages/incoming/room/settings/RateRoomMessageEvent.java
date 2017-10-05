package com.habboproject.server.network.messages.incoming.room.settings;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class RateRoomMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        if (!client.getPlayer().getEntity().canRateRoom()) {
            return;
        }

        room.getRatings().add(client.getPlayer().getId());

        int direction = msg.readInt();
        int score = room.getData().getScore();

        if (direction == 1)
            score++;
        else
            score--;

        room.getData().setScore(score);
        room.getEntities().refreshScore();

        room.getData().save();
    }
}
