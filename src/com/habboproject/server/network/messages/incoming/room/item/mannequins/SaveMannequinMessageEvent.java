package com.habboproject.server.network.messages.incoming.room.item.mannequins;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.boutique.MannequinFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SaveMannequinMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int id = msg.readInt();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || !room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        RoomItemFloor item = room.getItems().getFloorItem(id);

        if (item == null) {
            return;
        }

        ((MannequinFloorItem) item).setName(msg.readString());
        ((MannequinFloorItem) item).setGender(client.getPlayer().getData().getGender());

        room.getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
        item.saveData();
    }
}
