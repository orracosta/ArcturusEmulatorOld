package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.pets.PetTrainingPanelComposer;

/**
 * Created on 30-11-2014 22:09.
 */
public class RequestPetTrainingPanelEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int petId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        Pet pet = (Pet) this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);

        if(pet != null)
            this.client.sendResponse(new PetTrainingPanelComposer(pet));
    }
}
