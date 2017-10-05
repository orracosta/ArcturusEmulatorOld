package com.habboproject.server.network.messages.outgoing.room.pets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.pets.data.PetData;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class PetTrainingPanelMessageComposer extends MessageComposer {

    private final PetData petData;

    public PetTrainingPanelMessageComposer(final PetData petData) {
        this.petData = petData;
    }

    @Override
    public short getId() {
        return Composers.PetTrainingPanelMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.petData.getId());

        msg.writeInt(7);

        msg.writeInt(0);
        msg.writeInt(1);
        msg.writeInt(3);
        msg.writeInt(5);
        msg.writeInt(6);
        msg.writeInt(7);
        msg.writeInt(11);

        // for now we will enable 8 commands, we will move it to levelling up soon.
        msg.writeInt(7);

        // enabled commands
        msg.writeInt(0);
        msg.writeInt(1);
        msg.writeInt(3);
        msg.writeInt(5);
        msg.writeInt(6);
        msg.writeInt(7);
        msg.writeInt(11);
    }
}
