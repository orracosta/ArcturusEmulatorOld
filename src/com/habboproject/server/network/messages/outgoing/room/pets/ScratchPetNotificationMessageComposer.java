package com.habboproject.server.network.messages.outgoing.room.pets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class ScratchPetNotificationMessageComposer extends MessageComposer {

    private final PetEntity petEntity;

    public ScratchPetNotificationMessageComposer(PetEntity petEntity) {
        this.petEntity = petEntity;
    }

    @Override
    public short getId() {
        return Composers.RespectPetNotificationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.petEntity.getId());
        msg.writeInt(this.petEntity.getId());
        msg.writeInt(this.petEntity.getData().getId());

        msg.writeString(this.petEntity.getData().getName());
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeString(this.petEntity.getData().getColour());
        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(1);

    }
}
