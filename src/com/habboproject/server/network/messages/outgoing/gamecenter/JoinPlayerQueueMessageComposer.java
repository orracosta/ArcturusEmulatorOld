package com.habboproject.server.network.messages.outgoing.gamecenter;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 01/03/2017.
 */
public class JoinPlayerQueueMessageComposer extends MessageComposer {
    private final int gameId;

    public JoinPlayerQueueMessageComposer(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public short getId() {
        return 2621;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(gameId);
    }
}
