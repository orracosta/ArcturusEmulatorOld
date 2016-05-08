package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetInformationComposer extends MessageComposer
{
    private final AbstractPet pet;

    public PetInformationComposer(AbstractPet pet)
    {
        this.pet = pet;
    }

    @Override
    public ServerMessage compose()
    {
        double days = Math.floor((Emulator.getIntUnixTimestamp() - this.pet.getCreated()) / 3600);
        this.response.init(Outgoing.PetInformationComposer);
        this.response.appendInt32(this.pet.getId());
        this.response.appendString(this.pet.getName());
        if(this.pet instanceof MonsterplantPet)
        {
            this.response.appendInt32(3);
            this.response.appendInt32(7);
        }
        else
        {
            this.response.appendInt32(this.pet.getLevel()); //level
            this.response.appendInt32(20); //max level
        }
        this.response.appendInt32(this.pet.getExperience());
        this.response.appendInt32(PetManager.experiences[this.pet.getLevel() - 1]); //XP Goal
        this.response.appendInt32(this.pet.getEnergy());
        this.response.appendInt32(this.pet.getLevel() * 100); //Max energy
        this.response.appendInt32(0); //this.pet.getHappyness()
        this.response.appendInt32(100);
        this.response.appendInt32(this.pet.getRespect());
        this.response.appendInt32(this.pet.getUserId());
        this.response.appendInt32((int)days + 1);
        this.response.appendString(""); //Owner name
        this.response.appendInt32(this.pet instanceof MonsterplantPet ? 1 : 0);
        this.response.appendBoolean(this.pet instanceof HorsePet && ((HorsePet) this.pet).hasSaddle());
        this.response.appendBoolean(this.pet instanceof HorsePet && ((HorsePet) this.pet).getRider() != null);
        this.response.appendInt32(0);
        this.response.appendInt32(this.pet instanceof HorsePet && ((HorsePet) this.pet).anyoneCanRide() ? 1 : 0);
        this.response.appendBoolean(false); //State Grown
        this.response.appendBoolean(true); //unknown 1
        this.response.appendBoolean(false); //Dead
        this.response.appendInt32(this.pet instanceof MonsterplantPet ? ((MonsterplantPet) this.pet).getType() : 0);
        this.response.appendInt32(129600);
        this.response.appendInt32(128000);
        this.response.appendInt32(1000);
        this.response.appendBoolean(true); //unknown 3
        return this.response;
    }
}
