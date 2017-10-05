package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.PrivateChatFloorItem;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.entries.RoomChatLogEntry;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.ShoutMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ShoutMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        String message = msg.readString();
        int colour = msg.readInt();

        if (message.length() < 1) return;

        if (!TalkMessageEvent.isValidColour(colour, client)) {
            colour = 0;
        }

        String filteredMessage = TalkMessageEvent.filterMessage(message);

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                client.getLogger().info("Filter detected a blacklisted word in message: \"" + message + "\"");
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getMessage();
            }
        }

        if (client.getPlayer().getEntity().onChat(filteredMessage)) {
            try {
                if (LogManager.ENABLED)
                    LogManager.getInstance().getStore().getLogEntryContainer().put(new RoomChatLogEntry(client.getPlayer().getEntity().getRoom().getId(), client.getPlayer().getId(), message));
            } catch (Exception ignored) {

            }

            if(client.getPlayer().getEntity().getPrivateChatItemId() != 0) {
                // broadcast message only to players in the tent.
                RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(client.getPlayer().getEntity().getPrivateChatItemId());

                if(floorItem != null) {
                    ((PrivateChatFloorItem) floorItem).broadcastMessage(new ShoutMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), colour));
                }
            } else {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastChatMessage(new ShoutMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), colour), client.getPlayer().getEntity());
            }
        }

        client.getPlayer().getEntity().postChat(filteredMessage);

    }
}
