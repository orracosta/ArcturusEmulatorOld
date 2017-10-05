package com.habboproject.server.network.messages.incoming.catalog.purchase;

import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.catalog.types.gifts.GiftData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class PurchaseGiftMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int pageId = msg.readInt();
        int itemId = msg.readInt();

        String extraData = msg.readString();

        String sendingUser = msg.readString();
        String message = msg.readString();
        int spriteId = msg.readInt();
        int wrappingPaper = msg.readInt();
        int decorationType = msg.readInt();
        boolean showUsername = msg.readBoolean();

        if(!CatalogManager.getInstance().getGiftBoxesNew().contains(spriteId) && !CatalogManager.getInstance().getGiftBoxesOld().contains(spriteId)) {
            client.disconnect();
            return;
        }

        GiftData data = new GiftData(pageId, itemId, client.getPlayer().getId(), sendingUser, message, spriteId, wrappingPaper, decorationType, showUsername, extraData);

        CatalogManager.getInstance().getPurchaseHandler().purchaseItem(client, pageId, itemId, extraData, 1, data);
    }
}
