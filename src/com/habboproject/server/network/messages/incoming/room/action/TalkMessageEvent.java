package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.PrivateChatFloorItem;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.entries.RoomChatLogEntry;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.google.common.primitives.Ints;


public class TalkMessageEvent implements Event {
    private static int[] allowedColours = new int[]{
            0, 3, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 29, 37
    };

    public static boolean isValidColour(int colour, Session client) {
        if (!Ints.contains(allowedColours, colour)) {
            return false;
        }

        if ((colour == 23 || colour == 37) && !client.getPlayer().getPermissions().getRank().modTool())
            return false;

        return true;
    }

    public static String filterMessage(String message) {
        if (message.contains("You can type here to talk!")) {
            message = message.replace("You can type here to talk!", "");
        }

        return message.replace((char) 13 + "", "");
    }

    public void handle(Session client, MessageEvent msg) {
        String message = msg.readString();
        int colour = msg.readInt();

        PlayerEntity playerEntity = client.getPlayer().getEntity();

        if (playerEntity == null || playerEntity.getRoom() == null || playerEntity.getRoom().getEntities() == null)
            return;

        if (!playerEntity.isVisible() && !playerEntity.getPlayer().isInvisible()) {
            return;
        }

        if (!TalkMessageEvent.isValidColour(colour, client))
            colour = 0;

        String filteredMessage = filterMessage(message);

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(filteredMessage);

            if (filterResult.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getMessage();
            }
        }

        if (playerEntity.onChat(filteredMessage)) {
            try {
                if (LogManager.ENABLED && !message.replace(" ", "").isEmpty())
                    LogManager.getInstance().getStore().getLogEntryContainer().put(new RoomChatLogEntry(playerEntity.getRoom().getId(), client.getPlayer().getId(), message));
            } catch (Exception ignored) {

            }

            if (client.getPlayer().getEntity().getPrivateChatItemId() != 0) {
                // broadcast message only to players in the tent.
                RoomItemFloor floorItem = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(client.getPlayer().getEntity().getPrivateChatItemId());

                if (floorItem != null) {
                    ((PrivateChatFloorItem) floorItem).broadcastMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), colour));
                }
            } else {
                client.getPlayer().getEntity().getRoom().getEntities().broadcastChatMessage(new TalkMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage, RoomManager.getInstance().getEmotions().getEmotion(filteredMessage), colour), client.getPlayer().getEntity());
            }
        }

        playerEntity.postChat(filteredMessage);
    }
}