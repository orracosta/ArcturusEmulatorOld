package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 29-11-2014 17:10.
 */
public class AddPetComposer extends MessageComposer
{
    private final AbstractPet pet;

    public AddPetComposer(AbstractPet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.AddPetComposer);
        pet.serialize(this.response);
        this.response.appendBoolean(false);
        return this.response;
    }
}
