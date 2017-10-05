package com.habboproject.server.network.messages.outgoing.room.pets.breeding;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 19/03/2017.
 */
public class PetBreedingMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return 0;
    }

    @Override
    public void compose(IComposer msg) {

    }
}
