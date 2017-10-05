package com.habboproject.server.network.messages.incoming.user.profile;

import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.profile.UserBadgesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.inventory.InventoryDao;

import java.util.Map;


public class WearBadgeMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.getPlayer().getInventory().resetBadgeSlots();


        final long currentTimeMs = System.currentTimeMillis();
        final long timeSinceLastUpdate = currentTimeMs - client.getPlayer().getLastBadgeUpdate();

        if (timeSinceLastUpdate < 350) {
            return;
        }

        for (int i = 0; i < 5; i++) {
            final int slot = msg.readInt();
            final String badge = msg.readString();

            if (badge.isEmpty()) {
                continue;
            }

            if (!client.getPlayer().getInventory().getBadges().containsKey(badge) || slot < 1 || slot > 5) {
                return;
            }

            client.getPlayer().getInventory().getBadges().replace(badge, slot);
        }

        for (Map.Entry<String, Integer> badgeToUpdate : client.getPlayer().getInventory().getBadges().entrySet()) {
            InventoryDao.updateBadge(badgeToUpdate.getKey(), badgeToUpdate.getValue(), client.getPlayer().getId());
        }

        if (client.getPlayer().getEntity() != null) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UserBadgesMessageComposer(client.getPlayer().getId(), client.getPlayer().getInventory().equippedBadges()));
        } else {
            client.send(new UserBadgesMessageComposer(client.getPlayer().getId(), client.getPlayer().getInventory().equippedBadges()));
        }

        client.getPlayer().getQuests().progressQuest(QuestType.PROFILE_BADGE);
        client.getPlayer().setLastBadgeUpdate(currentTimeMs);
    }
}
