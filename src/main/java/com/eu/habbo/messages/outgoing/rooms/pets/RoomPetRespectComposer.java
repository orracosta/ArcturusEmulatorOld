package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 30-11-2014 16:09.
 */
public class RoomPetRespectComposer extends MessageComposer
{
    private final Pet pet;

    public RoomPetRespectComposer(Pet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomPetRespectComposer);
        this.response.appendInt32(1);
        this.response.appendInt32(100);
        pet.serialize(this.response);
        return this.response;
    }
}
