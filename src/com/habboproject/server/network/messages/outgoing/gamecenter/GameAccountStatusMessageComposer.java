package com.habboproject.server.network.messages.outgoing.gamecenter;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 01/03/2017.
 */
public class GameAccountStatusMessageComposer extends MessageComposer {
    private final int gameId;

    public GameAccountStatusMessageComposer(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public short getId() {
        return 2626;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(gameId);
        msg.writeInt(0); // open = 0, closed = 1
    }
}
