package com.habboproject.server.network.messages.incoming.catalog.ads;

import com.habboproject.server.api.game.rooms.settings.RoomAccessType;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.ads.CatalogPromotionGetRoomsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Lists;

import java.util.List;


public class CatalogPromotionGetRoomsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        List<RoomData> roomDataList = Lists.newArrayList();

        for (Integer roomId : client.getPlayer().getRooms()) {
            RoomData data = RoomManager.getInstance().getRoomData(roomId);

            if (data != null && data.getAccess() == RoomAccessType.OPEN) {
                roomDataList.add(data);
            }
        }

        client.send(new CatalogPromotionGetRoomsMessageComposer(roomDataList));
    }
}