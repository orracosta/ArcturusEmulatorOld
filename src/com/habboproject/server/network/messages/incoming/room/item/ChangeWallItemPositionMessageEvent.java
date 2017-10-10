package com.habboproject.server.network.messages.incoming.room.item;

import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateWallItemMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;


public class ChangeWallItemPositionMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int itemId = msg.readInt();
        String position = Position.validateWallPosition(msg.readString());

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || position == null) {
            return;
        }

        boolean isOwner = client.getPlayer().getId() == room.getData().getOwnerId() || client.getPlayer().getData().getRank() > 5;
        boolean hasRights = room.getRights().hasRights(client.getPlayer().getId());

        if (isOwner || hasRights || client.getPlayer().getPermissions().getRank().roomFullControl() || client.getPlayer().getPermissions().getRank().roomFullAcessPublic()) {
            RoomItemWall item = room.getItems().getWallItem(itemId);

            if (item == null) {
                return;
            }

            RoomItemDao.placeWallItem(room.getId(), position, (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : item.getExtraData(), item.getId());

            item.setPosition(position);
            room.getEntities().broadcastMessage(new UpdateWallItemMessageComposer(item, room.getData().getOwnerId(), room.getData().getOwner()));
        }
    }
}
