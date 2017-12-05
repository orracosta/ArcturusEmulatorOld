package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.IPetLook;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;

public class RoomPetComposer extends MessageComposer implements TIntObjectProcedure<AbstractPet>
{
    private final TIntObjectMap<AbstractPet> pets;

    public RoomPetComposer(AbstractPet pet)
    {
        this.pets = new TIntObjectHashMap<AbstractPet>();
        this.pets.put(pet.getId(), pet);
    }

    public RoomPetComposer(TIntObjectMap<AbstractPet> pets)
    {
        this.pets = pets;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUsersComposer);
        this.response.appendInt(this.pets.size());
        this.pets.forEachEntry(this);
        return this.response;
    }

    @Override
    public boolean execute(int a, AbstractPet pet)
    {
        this.response.appendInt(pet.getId());
        this.response.appendString(pet.getName());
        this.response.appendString("");
        if(pet instanceof IPetLook)
        {
            this.response.appendString(((IPetLook)pet).getLook());
        }
        else
        {
            this.response.appendString(pet.getPetData().getType() + " " + pet.getRace() + " " + pet.getColor() + " " + ((pet instanceof HorsePet ? (((HorsePet) pet).hasSaddle() ? "3" : "2") + " 2 " + ((HorsePet) pet).getHairStyle() + " " + ((HorsePet) pet).getHairColor() + " 3 " + ((HorsePet) pet).getHairStyle() + " " + ((HorsePet) pet).getHairColor() + (((HorsePet) pet).hasSaddle() ? " 4 9 0" : "") : pet instanceof MonsterplantPet ? (((MonsterplantPet) pet).look.isEmpty() ? "2 1 8 6 0 -1 -1" : ((MonsterplantPet) pet).look) : "2 2 -1 0 3 -1 0")));
        }
        this.response.appendInt(pet.getRoomUnit().getId());
        this.response.appendInt32(pet.getRoomUnit().getX());
        this.response.appendInt32(pet.getRoomUnit().getY());
        this.response.appendString(pet.getRoomUnit().getZ() + "");
        this.response.appendInt(0);
        this.response.appendInt(2);
        this.response.appendInt(pet.getPetData().getType());
        this.response.appendInt(pet.getUserId());
        this.response.appendString(pet.getRoom().getFurniOwnerNames().get(pet.getUserId()));
        this.response.appendInt(pet instanceof MonsterplantPet ? ((MonsterplantPet) pet).getRarity() : 1);
        this.response.appendBoolean(pet instanceof HorsePet && ((HorsePet) pet).hasSaddle());
        this.response.appendBoolean(false);
        this.response.appendBoolean((pet instanceof MonsterplantPet && ((MonsterplantPet) pet).canBreed())); //Has breeasasd//
        this.response.appendBoolean(!(pet instanceof MonsterplantPet && ((MonsterplantPet) pet).isFullyGrown())); //unknown 1
        this.response.appendBoolean(pet instanceof MonsterplantPet && ((MonsterplantPet) pet).isDead()); //Can revive // //Also disables fertilize when dead?
        this.response.appendBoolean(pet instanceof MonsterplantPet && ((MonsterplantPet) pet).isPubliclyBreedable()); //Breedable checkbox //Toggle breeding permission
        this.response.appendInt(pet instanceof MonsterplantPet ? ((MonsterplantPet) pet).getGrowthStage() : pet.getLevel());
        this.response.appendString("");

        return true;
    }
}
