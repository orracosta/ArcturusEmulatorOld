package com.eu.habbo.messages.outgoing.rooms.pets.breeding;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetBreedingResultComposer extends MessageComposer
{
    public final int anInt1;
    public final PetBreedingPet petOne;
    public final PetBreedingPet petTwo;

    public PetBreedingResultComposer(int anInt1, AbstractPet petOne, String ownerPetOne, AbstractPet petTwo, String ownerPetTwo)
    {
        this.anInt1 = anInt1;
        this.petOne = new PetBreedingPet(petOne, ownerPetOne);
        this.petTwo = new PetBreedingPet(petTwo, ownerPetTwo);
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PetBreedingResultComposer);
        this.response.appendInt(this.anInt1);
        this.petOne.serialize(this.response);
        this.petTwo.serialize(this.response);

        this.response.appendInt(5); //Levels
        {
            this.response.appendInt(1); //Percentage
            this.response.appendInt(1); //Count
            {
                this.response.appendInt(1); //Breed
            }
            this.response.appendInt(1); //Percentage
            this.response.appendInt(1); //Count
            {
                this.response.appendInt(1); //Breed
            }
            this.response.appendInt(1); //Percentage
            this.response.appendInt(1); //Count
            {
                this.response.appendInt(1); //Breed
            }
            this.response.appendInt(1); //Percentage
            this.response.appendInt(1); //Count
            {
                this.response.appendInt(1); //Breed
            }
        }

        this.response.appendInt(0); //Race type
        return this.response;
    }

    public class PetBreedingPet implements ISerialize
    {
        public final AbstractPet pet;
        public final String ownerName;

        public PetBreedingPet(AbstractPet pet, String ownerName)
        {
            this.pet = pet;
            this.ownerName = ownerName;
        }

        @Override
        public void serialize(ServerMessage message)
        {
            message.appendInt(this.pet.getId());
            message.appendString(this.pet.getName());
            message.appendInt(this.pet.getLevel());
            message.appendString(this.pet.getColor());
            message.appendString(this.ownerName);
        }
    }
}