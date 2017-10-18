package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetStatusUpdateComposer extends MessageComposer
{
    private final Pet pet;

    public PetStatusUpdateComposer(Pet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PetStatusUpdateComposer);
        this.response.appendInt(this.pet.getRoomUnit().getId());
        this.response.appendInt(this.pet instanceof HorsePet && ((HorsePet) this.pet).anyoneCanRide() ? 1 : 0);
        this.response.appendBoolean((this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).canBreed())); //unknown 1
        this.response.appendBoolean((this.pet instanceof MonsterplantPet && !((MonsterplantPet) this.pet).isFullyGrown()));
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isDead()); //State Grown
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isPubliclyBreedable());
        return this.response;
    }
}
