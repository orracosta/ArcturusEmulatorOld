package com.habboproject.server.network.messages.incoming.room.item.wired;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class UpdateSnapshotsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int itemId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) return;

        Room room = client.getPlayer().getEntity().getRoom();

        boolean isOwner = client.getPlayer().getId() == room.getData().getOwnerId();
        boolean hasRights = room.getRights().hasRights(client.getPlayer().getId());

        if ((!isOwner && !hasRights) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        RoomItemFloor floorItem = room.getItems().getFloorItem(itemId);

        if (floorItem == null || !(floorItem instanceof WiredItemSnapshot.Refreshable)) {
            return;
        }

        ((WiredItemSnapshot.Refreshable) floorItem).refreshSnapshots();
    }
}
