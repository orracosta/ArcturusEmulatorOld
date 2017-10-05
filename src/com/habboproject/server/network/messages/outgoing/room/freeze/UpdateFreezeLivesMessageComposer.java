package com.habboproject.server.network.messages.outgoing.room.freeze;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

/**
 * Created by brend on 04/02/2017.
 */
public class UpdateFreezeLivesMessageComposer extends MessageComposer {
    private final int playerId;
    private final int freezeLives;

    public UpdateFreezeLivesMessageComposer(int playerId, int freezeLives) {
        this.playerId = playerId;
        this.freezeLives = freezeLives;
    }

    @Override
    public short getId() {
        return Composers.UpdateFreezeLivesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.playerId);
        msg.writeInt(this.freezeLives);
    }
}
