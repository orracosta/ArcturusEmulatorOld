package com.habboproject.server.network.messages.incoming.room.floor;

import com.habboproject.server.game.rooms.models.RoomModel;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.floor.FloorPlanDoorMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GetFloorPlanDoorMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() != null) {
            RoomModel model = client.getPlayer().getEntity().getRoom().getModel();

            if (model == null) return;

            client.send(new FloorPlanDoorMessageComposer(model.getDoorX(), model.getDoorY(), model.getDoorRotation()));
        }
    }
}
