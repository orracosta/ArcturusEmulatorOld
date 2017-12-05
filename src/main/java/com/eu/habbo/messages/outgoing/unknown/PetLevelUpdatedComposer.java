package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetLevelUpdatedComposer extends MessageComposer
{
    private final AbstractPet pet;
    private final int level;

    public PetLevelUpdatedComposer(AbstractPet pet, int level)
    {
        this.pet = pet;
        this.level = level;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PetLevelUpdatedComposer);
        this.response.appendInt(this.pet.getRoomUnit().getId());
        this.response.appendInt(this.pet.getId());
        this.response.appendInt(this.level);
        return this.response;
    }
}