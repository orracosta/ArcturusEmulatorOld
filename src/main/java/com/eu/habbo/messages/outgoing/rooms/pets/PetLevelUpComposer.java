package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-2-2015 12:21.
 */
public class PetLevelUpComposer extends MessageComposer
{
    private final AbstractPet pet;

    public PetLevelUpComposer(AbstractPet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PetLevelUpComposer);
        this.response.appendInt32(this.pet.getId());
        this.response.appendString(this.pet.getName());
        this.response.appendInt32(this.pet.getLevel());
        this.response.appendInt32(this.pet.getPetData().getType());
        this.response.appendInt32(this.pet.getRace());
        this.response.appendString(this.pet.getColor());
        this.response.appendInt32(0);
        this.response.appendInt32(0);

        //:test 2329  i:0 s:a i:3 i:1 i:1 s:FF00FF i:0 i:0
        return this.response;
    }
}
