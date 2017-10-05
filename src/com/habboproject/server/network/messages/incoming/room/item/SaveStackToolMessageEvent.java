package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.MagicStackFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SaveStackToolMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null) {
            return;
        }
        if (!room.getRights().hasRights(client.getPlayer().getEntity().getPlayerId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            client.disconnect();
            return;
        }

        if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId())
                && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        int itemId = msg.readInt();
        int height = msg.readInt();

        if (height < 0 && height != -100) {
            return;
        }

        RoomItemFloor floorItem = room.getItems().getFloorItem(itemId);

        if (floorItem == null || !(floorItem instanceof MagicStackFloorItem)) return;

        MagicStackFloorItem magicStackFloorItem = ((MagicStackFloorItem) floorItem);

        if (height == -100) {
            magicStackFloorItem.setMagicHeight(
                    magicStackFloorItem.getRoom().getMapping().getTile(
                            magicStackFloorItem.getPosition().getX(),
                            magicStackFloorItem.getPosition().getY()
                    ).getOriginalHeight());
        } else {
            magicStackFloorItem.setMagicHeight(height / 100.0d);
        }

        for (AffectedTile affectedTile : AffectedTile.getAffectedBothTilesAt(
                magicStackFloorItem.getDefinition().getLength(),
                magicStackFloorItem.getDefinition().getWidth(),
                magicStackFloorItem.getPosition().getX(),
                magicStackFloorItem.getPosition().getY(),
                magicStackFloorItem.getRotation())) {

            RoomTile tile = magicStackFloorItem.getRoom().getMapping().getTile(affectedTile.x, affectedTile.y);

            if (tile != null) {
                tile.reload();

                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tile));
            }
        }

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(magicStackFloorItem));
        magicStackFloorItem.saveData();
    }
}
