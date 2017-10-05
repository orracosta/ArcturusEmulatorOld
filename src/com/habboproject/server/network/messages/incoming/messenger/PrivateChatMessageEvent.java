package com.habboproject.server.network.messages.incoming.messenger;

import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.players.components.types.messenger.MessengerFriend;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.entries.MessengerChatLogEntry;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.messenger.InstantChatMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class PrivateChatMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int userId = msg.readInt();
        String message = msg.readString();

        if (userId == Integer.MAX_VALUE && client.getPlayer().getPermissions().getRank().messengerStaffChat()) {
            for (Session player : ModerationManager.getInstance().getModerators()) {
                if (player == client) continue;
                player.send(new InstantChatMessageComposer(client.getPlayer().getData().getUsername() + ": " + message, Integer.MAX_VALUE));
            }
            return;
        }

        MessengerFriend friend = client.getPlayer().getMessenger().getFriendById(userId);

        if (friend == null) {
            return;
        }

        Session friendClient = friend.getSession();

        if (friendClient == null) {
            return;
        }

        final long time = System.currentTimeMillis();

        if (!client.getPlayer().getPermissions().getRank().floodBypass()) {
            if (time - client.getPlayer().getMessengerLastMessageTime() < 750) {
                client.getPlayer().setMessengerFloodFlag(client.getPlayer().getMessengerFloodFlag() + 1);

                if (client.getPlayer().getMessengerFloodFlag() >= 4) {
                    client.getPlayer().setMessengerFloodTime(client.getPlayer().getPermissions().getRank().floodTime());
                    client.getPlayer().setMessengerFloodFlag(0);

                }
            }

            if (client.getPlayer().getMessengerFloodTime() >= 1) {
                return;
            }

            client.getPlayer().setMessengerLastMessageTime(time);
        }

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                return;
            } else if (filterResult.wasModified()) {
                message = filterResult.getMessage();
            }
        }

        try {
            if (LogManager.ENABLED && CometSettings.messengerLogMessages)
                LogManager.getInstance().getStore().getLogEntryContainer().put(new MessengerChatLogEntry(client.getPlayer().getId(), userId, message));
        } catch (Exception ignored) {

        }


        friendClient.send(new InstantChatMessageComposer(message, client.getPlayer().getId()));
    }
}
