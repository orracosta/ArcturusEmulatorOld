package com.habboproject.server.network.messages.outgoing.room.pets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class AddExperiencePointsMessageComposer extends MessageComposer {

    private final int petId;
    private final int virtualId;
    private final int amount;

    public AddExperiencePointsMessageComposer(int petId, int virtualId, int amount) {
        this.petId = petId;
        this.virtualId = virtualId;
        this.amount = amount;
    }

    @Override
    public short getId() {
        return Composers.AddExperiencePointsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.petId);
        msg.writeInt(this.virtualId);
        msg.writeInt(this.amount);
    }
}
