package com.habboproject.server.network.messages.incoming.user.inventory;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.inventory.BadgeInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.profile.UserBadgesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.inventory.InventoryDao;


public class BadgeInventoryMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int userId = msg.readInt();

        if (userId == client.getPlayer().getId()) {
            client.send(new BadgeInventoryMessageComposer(client.getPlayer().getInventory().getBadges()));
            return;
        }

        client.send(new UserBadgesMessageComposer(userId, InventoryDao.getWornBadgesByPlayerId(userId)));
    }
}
