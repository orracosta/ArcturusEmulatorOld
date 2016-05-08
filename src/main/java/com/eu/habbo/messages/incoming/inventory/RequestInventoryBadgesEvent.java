package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryAchievementsComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryBadgesComposer;

/**
 * Created on 28-8-2014 19:14.
 */
public class RequestInventoryBadgesEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new InventoryBadgesComposer(this.client.getHabbo()));
        this.client.sendResponse(new InventoryAchievementsComposer());
    }
}
