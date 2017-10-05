package com.habboproject.server.network.messages.incoming.catalog.ads;

import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.RoomPromotion;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.events.RoomPromotionMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.rooms.RoomDao;


public class PromotionUpdateMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int id = msg.readInt();
        String promotionName = msg.readString();
        String promotionDescription = msg.readString();

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        RoomPromotion roomPromotion = room.getPromotion();

        if (roomPromotion != null) {
            roomPromotion.setPromotionName(promotionName);
            roomPromotion.setPromotionDescription(promotionDescription);

            RoomDao.updatePromotedRoom(roomPromotion);

            room.getEntities().broadcastMessage(new RoomPromotionMessageComposer(room.getData(), roomPromotion));
        }
    }
}
