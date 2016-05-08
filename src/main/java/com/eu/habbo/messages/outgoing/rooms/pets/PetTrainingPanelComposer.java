package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 30-11-2014 22:12.
 */
public class PetTrainingPanelComposer extends MessageComposer
{
    private final Pet pet;

    public PetTrainingPanelComposer(Pet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        List<PetCommand> enabled = new ArrayList<PetCommand>();
        Collections.sort(this.pet.getPetData().getPetCommands());

        this.response.init(Outgoing.PetTrainingPanelComposer);
        this.response.appendInt32(this.pet.getId());
        this.response.appendInt32(this.pet.getPetData().getPetCommands().size());

        for(PetCommand petCommand : this.pet.getPetData().getPetCommands())
        {
            this.response.appendInt32(petCommand.id);

            if(this.pet.getLevel() >= petCommand.level)
            {
                enabled.add(petCommand);
            }
        }

        Collections.sort(enabled);

        this.response.appendInt32(enabled.size());

        for(PetCommand petCommand : enabled)
        {
            this.response.appendInt32(petCommand.id);
        }

        return this.response;
    }
}
