package com.habboproject.server.network.messages.incoming.room.trading;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class TradingOfferItemsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int amount = msg.readInt();

        if(amount > 100) {
            amount = 100;
        }

        final long itemId = ItemManager.getInstance().getItemIdByVirtualId(msg.readInt());

        PlayerItem item = client.getPlayer().getInventory().getFloorItem(itemId);

        if (item == null) {
            item = client.getPlayer().getInventory().getWallItem(itemId);

            if (item == null) {
                return;
            }
        }

        Trade trade = client.getPlayer().getEntity().getRoom().getTrade().get(client.getPlayer().getEntity());
        if (trade == null) return;

        int i = 0;

        for(PlayerItem playerItem : client.getPlayer().getInventory().getFloorItems().values()) {
            if(i >= amount)
                break;

            if (playerItem.getBaseId() == item.getBaseId() && !trade.isOffered(playerItem)) {
                i++;

                trade.addItem(trade.getUserNumber(client.getPlayer().getEntity()), playerItem, false);
            }
        }

        trade.updateWindow();
    }
}
