package com.habboproject.server.network.messages.incoming.room.trading;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SendOfferMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        long itemId = ItemManager.getInstance().getItemIdByVirtualId(msg.readInt());
        PlayerItem item = client.getPlayer().getInventory().getFloorItem(itemId);

        if (item == null) {
            item = client.getPlayer().getInventory().getWallItem(itemId);

            if (item == null) {
                return;
            }
        }

        Trade trade = client.getPlayer().getEntity().getRoom().getTrade().get(client.getPlayer().getEntity());
        if (trade == null) return;

        trade.addItem(trade.getUserNumber(client.getPlayer().getEntity()), item, true);
    }
}
