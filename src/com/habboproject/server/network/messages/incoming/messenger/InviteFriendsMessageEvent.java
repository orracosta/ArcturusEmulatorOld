package com.habboproject.server.network.messages.incoming.messenger;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.messenger.InviteFriendMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.util.ArrayList;
import java.util.List;


public class InviteFriendsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final long time = System.currentTimeMillis();

        if (!client.getPlayer().getPermissions().getRank().floodBypass()) {
            if (time - client.getPlayer().getMessengerLastMessageTime() < 750) {
                client.getPlayer().setMessengerFloodFlag(client.getPlayer().getMessengerFloodFlag() + 1);

                if (client.getPlayer().getMessengerFloodFlag() >= 3) {
                    client.getPlayer().setMessengerFloodTime(client.getPlayer().getPermissions().getRank().floodTime());
                    client.getPlayer().setMessengerFloodFlag(0);

                }
            }

            if (client.getPlayer().getMessengerFloodTime() >= 1) {
                return;
            }

            client.getPlayer().setMessengerLastMessageTime(time);
        }

        int friendCount = msg.readInt();
        List<Integer> friends = new ArrayList<>();

        for (int i = 0; i < friendCount; i++) {
            friends.add(msg.readInt());
        }

        String message = msg.readString();

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                return;
            } else if (filterResult.wasModified()) {
                message = filterResult.getMessage();
            }
        }

        client.getPlayer().getMessenger().broadcast(friends, new InviteFriendMessageComposer(message, client.getPlayer().getId()));

        friends.clear();
    }
}
