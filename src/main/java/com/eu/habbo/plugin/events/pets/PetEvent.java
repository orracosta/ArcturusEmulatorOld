package com.eu.habbo.plugin.events.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.plugin.Event;

public abstract class PetEvent extends Event
{
    /**
     * The pet this event applies to.
     */
    public final AbstractPet pet;

    /**
     * Abstract class for events related to pets.
     * @param pet The pet this event applies to.
     */
    public PetEvent(AbstractPet pet)
    {
        this.pet = pet;
    }
}