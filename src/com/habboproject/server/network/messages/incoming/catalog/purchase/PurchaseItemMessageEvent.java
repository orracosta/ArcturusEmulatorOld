package com.habboproject.server.network.messages.incoming.catalog.purchase;

import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class PurchaseItemMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int pageId = msg.readInt();
        int itemId = msg.readInt();
        String data = msg.readString();
        int amount = msg.readInt();

        CatalogManager.getInstance().getPurchaseHandler().purchaseItem(client, pageId, itemId, data, amount, null);
    }
}
