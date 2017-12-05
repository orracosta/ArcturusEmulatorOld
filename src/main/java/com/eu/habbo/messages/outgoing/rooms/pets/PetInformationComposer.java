package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class PetInformationComposer extends MessageComposer
{
    private final AbstractPet pet;
    private final Room room;

    public PetInformationComposer(AbstractPet pet, Room room)
    {
        this.pet = pet;
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        double days = Math.floor((Emulator.getIntUnixTimestamp() - this.pet.getCreated()) / (3600 * 24));
        this.response.init(Outgoing.PetInformationComposer);
        this.response.appendInt(this.pet.getId());
        this.response.appendString(this.pet.getName());
        if(this.pet instanceof MonsterplantPet)
        {
            this.response.appendInt(((MonsterplantPet) this.pet).getGrowthStage()); //This equal
            this.response.appendInt(7);                                             //... to this means breedable
        }
        else
        {
            this.response.appendInt(this.pet.getLevel()); //level
            this.response.appendInt(20); //max level
        }
        this.response.appendInt(this.pet.getExperience());
        this.response.appendInt(PetManager.experiences[this.pet.getLevel() - 1]); //XP Goal
        this.response.appendInt(this.pet.getEnergy());
        this.response.appendInt(this.pet.getMaxEnergy()); //Max energy
        this.response.appendInt(this.pet.getHappyness()); //this.pet.getHappyness()
        this.response.appendInt(100);
        this.response.appendInt(this.pet.getRespect());
        this.response.appendInt(this.pet.getUserId());
        this.response.appendInt((int) days + 1);
        this.response.appendString(this.room.getFurniOwnerName(pet.getUserId())); //Owner name

        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet) this.pet).getRarity() : 0);
        this.response.appendBoolean(this.pet instanceof HorsePet && ((HorsePet) this.pet).hasSaddle());
        this.response.appendBoolean(this.pet instanceof HorsePet && ((HorsePet) this.pet).getRider() != null);
        this.response.appendInt(0);
        this.response.appendInt(this.pet instanceof HorsePet && ((HorsePet) this.pet).anyoneCanRide() ? 1 : 0);
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).canBreed()); //State Grown
        this.response.appendBoolean(!(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isFullyGrown())); //unknown 1
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isDead()); //Dead
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet) this.pet).getRarity() : 0);
        this.response.appendInt(MonsterplantPet.timeToLive); //Maximum wellbeing
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet) this.pet).remainingTimeToLive() : 0); //Remaining Wellbeing
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet) this.pet).remainingGrowTime() : 0);
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet) this.pet).isPubliclyBreedable()); //Breedable checkbox


        /*
        public function get _SafeStr_6187():Boolean
        {
            return (this._SafeStr_10925);
        }
        public function get _SafeStr_6188():Boolean
        {
            return (this._SafeStr_10926);
        }
        public function get _SafeStr_6189():Boolean
        {
            return (this._SafeStr_10927);
        }
        public function get _SafeStr_6190():Boolean
        {
            return (this._SafeStr_10928);
        }
         */
        return this.response;
    }
}
