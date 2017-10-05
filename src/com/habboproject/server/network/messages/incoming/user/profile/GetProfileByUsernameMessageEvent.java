package com.habboproject.server.network.messages.incoming.user.profile;

import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.game.players.types.PlayerStatistics;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.user.profile.LoadProfileMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.groups.GroupDao;
import com.habboproject.server.storage.queries.player.PlayerDao;

import java.util.List;


public class GetProfileByUsernameMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String username = msg.readString();

        PlayerData data = username.equals(client.getPlayer().getData().getUsername()) ? client.getPlayer().getData() : null;
        PlayerStatistics stats = data != null ? client.getPlayer().getStats() : null;
        List<Integer> groups = data != null ? client.getPlayer().getGroups() : null;

        if (data == null) {
            if (NetworkManager.getInstance().getSessions().getByPlayerUsername(username) != null) {
                data = NetworkManager.getInstance().getSessions().getByPlayerUsername(username).getPlayer().getData();
                stats = NetworkManager.getInstance().getSessions().getByPlayerUsername(username).getPlayer().getStats();
                groups = NetworkManager.getInstance().getSessions().getByPlayerUsername(username).getPlayer().getGroups();
            }
        }

        if (data == null) {
            int id = PlayerDao.getIdByUsername(username);
            data = PlayerManager.getInstance().getDataByPlayerId(id);
            stats = PlayerDao.getStatisticsById(id);
            groups = GroupDao.getIdsByPlayerId(id);
        }

        if (data == null) {
            return;
        }

        client.send(new LoadProfileMessageComposer(data, stats, groups, client.getPlayer().getMessenger().getFriendById(data.getId()) != null, false));

    }
}
