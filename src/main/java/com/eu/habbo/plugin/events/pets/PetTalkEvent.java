package com.eu.habbo.plugin.events.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;

public class PetTalkEvent extends PetEvent
{
    /**
     * The message to be displayed
     */
    public RoomChatMessage message;

    public PetTalkEvent(AbstractPet pet, RoomChatMessage message)
    {
        super(pet);
        
        this.message = message;
    }
}
