package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.FloodFilterMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ApplyActionMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        if (client.getPlayer() != null && client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            int actionId = msg.readInt();

            if (actionId == 5) {
                client.getPlayer().getEntity().setIdle();
            } else {
                client.getPlayer().getEntity().unIdle();
            }

            if (actionId == 1) {
                client.getPlayer().getQuests().progressQuest(QuestType.SOCIAL_WAVE);
            }

            if(!client.getPlayer().getEntity().isVisible()) {
                return;
            }

            final long time = System.currentTimeMillis();

            if (!client.getPlayer().getPermissions().getRank().floodBypass()) {
                if (time - client.getPlayer().getRoomLastMessageTime() < 750) {
                    client.getPlayer().setRoomFloodFlag(client.getPlayer().getRoomFloodFlag() + 1);

                    if (client.getPlayer().getRoomFloodFlag() >= 3) {
                        client.getPlayer().setRoomFloodTime(client.getPlayer().getPermissions().getRank().floodTime());
                        client.getPlayer().setRoomFloodFlag(0);

                        client.getPlayer().getSession().send(new FloodFilterMessageComposer(client.getPlayer().getRoomFloodTime()));
                    }
                } else {
                    client.getPlayer().setRoomFloodFlag(0);
                }

                if (client.getPlayer().getRoomFloodTime() >= 1) {
                    return;
                }

                client.getPlayer().setRoomLastMessageTime(time);
            }

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new ActionMessageComposer(client.getPlayer().getEntity().getId(), actionId));
        }
    }
}
