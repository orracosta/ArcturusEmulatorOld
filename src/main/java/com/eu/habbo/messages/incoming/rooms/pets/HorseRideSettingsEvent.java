package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetHorseFigureComposer;

public class HorseRideSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int petId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        AbstractPet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);

        if(pet == null || pet.getUserId() != this.client.getHabbo().getHabboInfo().getId() || !(pet instanceof HorsePet))
            return;

        ((HorsePet) pet).setAnyoneCanRide(!((HorsePet) pet).anyoneCanRide());
        ((HorsePet) pet).needsUpdate = true;

        this.client.sendResponse(new RoomPetHorseFigureComposer((HorsePet) pet));
    }
}
