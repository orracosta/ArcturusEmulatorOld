package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class RemovePetComposer extends MessageComposer
{
    private final int petId;

    public RemovePetComposer(int petId)
    {
        this.petId = petId;
    }

    public RemovePetComposer(AbstractPet pet)
    {
        this.petId = pet.getId();
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RemovePetComposer);
        this.response.appendInt(this.petId);
        return this.response;
    }
}
