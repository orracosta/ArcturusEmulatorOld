package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 10-12-2014 10:20.
 */
public class RoomPetHorseFigureComposer extends MessageComposer
{
    private final HorsePet pet;

    public RoomPetHorseFigureComposer(HorsePet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomPetHorseFigureComposer);
        this.response.appendInt32(this.pet.getRoomUnit().getId());
        this.response.appendInt32(this.pet.getId());
        this.response.appendInt32(this.pet.getPetData().getType());
        this.response.appendInt32(this.pet.getRace());
        this.response.appendString(this.pet.getColor().toLowerCase());

        if(this.pet.hasSaddle())
        {
            this.response.appendInt32(2);
            this.response.appendInt32(3);
            this.response.appendInt32(4);
            this.response.appendInt32(9);
            this.response.appendInt32(0);
            this.response.appendInt32(3);

            this.response.appendInt32(this.pet.getHairStyle());
            this.response.appendInt32(this.pet.getHairColor());
            this.response.appendInt32(3);
            this.response.appendInt32(this.pet.getHairStyle());
            this.response.appendInt32(this.pet.getHairColor());
        }
        else
        {
            this.response.appendInt32(1);
            this.response.appendInt32(2);
            this.response.appendInt32(2);
            this.response.appendInt32(this.pet.getHairStyle());
            this.response.appendInt32(this.pet.getHairColor());
            this.response.appendInt32(3);
            this.response.appendInt32(this.pet.getHairStyle());
            this.response.appendInt32(this.pet.getHairColor());
        }
        this.response.appendBoolean(this.pet.hasSaddle());
        this.response.appendBoolean(this.pet.anyoneCanRide());
        return this.response;
    }
}
