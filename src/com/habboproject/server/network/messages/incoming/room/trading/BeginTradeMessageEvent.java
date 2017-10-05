package com.habboproject.server.network.messages.incoming.room.trading;

import com.habboproject.server.api.game.rooms.settings.RoomTradeState;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.trading.TradeErrorMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class BeginTradeMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer().getEntity() == null)
            return;

        int userId = msg.readInt();

        if (client.getPlayer().getEntity().getRoom().getData().getTradeState() == RoomTradeState.DISABLED) {
            client.send(new TradeErrorMessageComposer(6, ""));
            return;
        }

        PlayerEntity entity = (PlayerEntity) client.getPlayer().getEntity().getRoom().getEntities().getEntity(userId);

        if (entity == null || entity.hasStatus(RoomEntityStatus.TRADE) || entity.getPlayer() == null || entity.getPlayer().getSession() == null) {
            client.send(new TradeErrorMessageComposer(8, entity != null ? entity.getUsername() : "Unknown Player"));
            return;
        }

        if(entity.getPlayer().getSettings() != null && !entity.getPlayer().getSettings().getAllowTrade()) {
            client.send(new TradeErrorMessageComposer(4, entity.getUsername()));
            return;
        }

        if (client.getPlayer().getEntity().getRoom().getData().getOwnerId() != client.getPlayer().getId() && entity.getRoom().getData().getTradeState() == RoomTradeState.OWNER_ONLY) {
            client.send(new TradeErrorMessageComposer(6, ""));
            return;
        }

        long currentTime = System.currentTimeMillis();

        if(client.getPlayer().getLastTradeFlood() != 0) {
            long timeFloodEnds = client.getPlayer().getLastTradeTime() + ((client.getPlayer().getLastTradeFlag() * 1000));

            if(currentTime >= timeFloodEnds) {
                client.getPlayer().setLastTradeFlood(0);
            } else {
                return;
            }
        }

        if((currentTime - client.getPlayer().getLastTradeTime()) < 750) {
            client.getPlayer().setLastTradeTime(currentTime);

            if(client.getPlayer().getLastTradeFlag() >= 3) {
                client.getPlayer().setLastTradeFlood(30);
                return;
            }

            client.getPlayer().setLastTradeFlag(client.getPlayer().getLastTradeFlag() + 1);
        }

        client.getPlayer().getEntity().getRoom().getTrade().add(new Trade(client.getPlayer().getEntity(), entity));
    }
}
