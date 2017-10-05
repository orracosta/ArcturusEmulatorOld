package com.habboproject.server.network.messages.incoming.messenger;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.messenger.FriendRequestMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.storage.queries.player.messenger.MessengerDao;


public class RequestFriendshipMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        String username = msg.readString();

        if (username.equals(client.getPlayer().getData().getUsername()))
            return;

        Session request = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (request == null || request.getPlayer() == null || request.getPlayer().getMessenger() == null) return;

        if (!request.getPlayer().getSettings().getAllowFriendRequests()) {
            client.send(new AdvancedAlertMessageComposer(Locale.get("game.messenger.friendrequests.disabled")));
            return;
        }

        if (request.getPlayer().getMessenger().hasRequestFrom(client.getPlayer().getId()))
            return;

        request.getPlayer().getMessenger().addRequest(client.getPlayer().getId());
        request.send(new FriendRequestMessageComposer(client.getPlayer().getData()));

        int userId = PlayerDao.getIdByUsername(username);

        if (userId == 0)
            return;

        client.getPlayer().getQuests().progressQuest(QuestType.SOCIAL_FRIEND);
        MessengerDao.createRequest(client.getPlayer().getId(), userId);
    }
}