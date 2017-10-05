package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.profile.UserBadgesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class UserBadgesMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int userId = msg.readInt();

        if (client.getPlayer().getId() == userId) {
            client.send(new UserBadgesMessageComposer(client.getPlayer().getId(), client.getPlayer().getInventory().equippedBadges()));
            return;
        }

        if (client.getPlayer().getEntity() == null)
            return;


        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        PlayerEntity playerEntity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByPlayerId(userId);

        if (playerEntity != null) {
            if (playerEntity.getPlayer() == null || playerEntity.getPlayer().getInventory() == null) return;

            client.send(new UserBadgesMessageComposer(playerEntity.getPlayerId(), playerEntity.getPlayer().getInventory().equippedBadges()));
        }
    }
}