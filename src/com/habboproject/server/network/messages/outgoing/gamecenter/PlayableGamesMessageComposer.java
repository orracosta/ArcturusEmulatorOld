package com.habboproject.server.network.messages.outgoing.gamecenter;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 05/03/2017.
 */
public class PlayableGamesMessageComposer extends MessageComposer {
    private final int gameId;

    public PlayableGamesMessageComposer(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public short getId() {
        return Composers.PlayableGamesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(gameId);
        msg.writeInt(-1);
        msg.writeInt(0);
    }
}
