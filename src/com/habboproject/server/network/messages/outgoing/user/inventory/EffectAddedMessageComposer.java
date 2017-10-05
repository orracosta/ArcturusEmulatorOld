package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 04/02/2017.
 */
public class EffectAddedMessageComposer extends MessageComposer {
    private final int effectId;
    private final int duration;
    private final boolean isTotem;

    public EffectAddedMessageComposer(int effectId, int duration, boolean isTotem) {
        this.effectId = effectId;
        this.duration = duration;
        this.isTotem = isTotem;
    }

    @Override
    public short getId() {
        return 1533;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.effectId);
        msg.writeInt(0);
        msg.writeInt(this.duration);
        msg.writeBoolean(this.isTotem);
    }
}
