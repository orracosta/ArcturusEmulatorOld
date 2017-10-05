package com.habboproject.server.network.messages.incoming.gamecenter;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.gamecenter.LoadGameMessageComposer;
import com.habboproject.server.network.messages.outgoing.gamecenter.JoinPlayerQueueMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.games.GameDao;

/**
 * Created by brend on 01/03/2017.
 */
public class JoinPlayerQueueMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int gameId = msg.readInt();

        client.send(new JoinPlayerQueueMessageComposer(gameId));

        client.send(new LoadGameMessageComposer(gameId, Comet.getServer().getConfig().get("comet.fastfood.host"),
                Integer.parseInt(Comet.getServer().getConfig().get("comet.fastfood.port")),
                Comet.getServer().getConfig().get("comet.fastfood.server"),
                GameDao.generatePlayerToken(client.getPlayer().getId())));
    }
}
