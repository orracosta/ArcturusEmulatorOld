package com.habboproject.server.network.websocket.messages.executors;

import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.messenger.FriendRequestMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.network.websocket.messages.WebSocketCommandExecutor;
import com.habboproject.server.network.websocket.messages.instances.RequestFriendshipCommandInstance;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.storage.queries.player.messenger.MessengerDao;

/**
 * Created by brend on 02/03/2017.
 */
public class RequestFriendshipCommandExecutor extends WebSocketCommandExecutor<RequestFriendshipCommandInstance> {
    public RequestFriendshipCommandExecutor() {
        super(RequestFriendshipCommandInstance.class);
    }

    @Override
    public void handle(RequestFriendshipCommandInstance instance) {
        final PlayerAvatar peddingPlayer = PlayerManager.getInstance().getAvatarByPlayerId(instance.getPeddingId(),
                PlayerAvatar.USERNAME_FIGURE);

        final PlayerAvatar requestFriend = PlayerManager.getInstance().getAvatarByPlayerId(instance.getRequestId(),
                PlayerAvatar.USERNAME_FIGURE);

        if (peddingPlayer == null || requestFriend == null) {
            return;
        }

        Session request = NetworkManager.getInstance().getSessions().getByPlayerUsername(requestFriend.getUsername());

        if (request == null || request.getPlayer() == null || request.getPlayer().getMessenger() == null) return;

        Session client = NetworkManager.getInstance().getSessions().getByPlayerUsername(peddingPlayer.getUsername());

        if (client == null || client.getPlayer() == null || client.getPlayer().getMessenger() == null) return;

        if (request.getPlayer().getMessenger().hasRequestFrom(client.getPlayer().getId()))
            return;

        request.getPlayer().getMessenger().addRequest(client.getPlayer().getId());
        request.send(new FriendRequestMessageComposer(client.getPlayer().getData()));

        int userId = PlayerDao.getIdByUsername(requestFriend.getUsername());

        if (userId == 0)
            return;

        client.getPlayer().getQuests().progressQuest(QuestType.SOCIAL_FRIEND);

        MessengerDao.createRequest(client.getPlayer().getId(), userId);
    }
}
