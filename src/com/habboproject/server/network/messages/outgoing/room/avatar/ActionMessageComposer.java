package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class ActionMessageComposer extends MessageComposer {
    private final int playerId;
    private final int actionId;

    public ActionMessageComposer(final int playerId, final int actionId) {
        this.playerId = playerId;
        this.actionId = actionId;
    }

    @Override
    public short getId() {
        return Composers.ActionMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeInt(actionId);
    }
}