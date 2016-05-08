package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetStatusUpdateComposer extends MessageComposer
{
    private final Pet pet;

    @Deprecated
    public PetStatusUpdateComposer(Pet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PetStatusUpdateComposer);
        this.response.appendInt32(this.pet.getId());
        this.response.appendBoolean(true); //anyone can ride.
        this.response.appendBoolean(true); //unknown 1
        this.response.appendBoolean(true); //unknown 2
        this.response.appendBoolean(true); //unknown 3
        return this.response;
    }
}
