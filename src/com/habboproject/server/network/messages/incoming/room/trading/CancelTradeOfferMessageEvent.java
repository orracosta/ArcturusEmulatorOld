package com.habboproject.server.network.messages.incoming.room.trading;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class CancelTradeOfferMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int itemId = msg.readInt();
        PlayerItem item = client.getPlayer().getInventory().getFloorItem(itemId);

        if (item == null) {
            item = client.getPlayer().getInventory().getWallItem(itemId);
        }

        Trade trade = client.getPlayer().getEntity().getRoom().getTrade().get(client.getPlayer().getEntity());
        if (trade == null) return;

        trade.removeItem(trade.getUserNumber(client.getPlayer().getEntity()), item);
    }
}
