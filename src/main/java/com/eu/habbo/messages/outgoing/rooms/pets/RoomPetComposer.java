package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;

/**
 * Created on 30-11-2014 12:32.
 */
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
        this.response.appendInt32(this.pets.size());
        this.pets.forEachEntry(this);
        return this.response;
    }

    @Override
    public boolean execute(int a, AbstractPet pet)
    {
        this.response.appendInt32(pet.getId());
        this.response.appendString(pet.getName());
        this.response.appendString("");
        if(pet instanceof MonsterplantPet)
        {
            //Shape (Name 1)
            //Color (Name 2)

            String look = "16 0 ffffff 5 2 1 2 1 1 1 1 1 1 1 1 1 1 1 1";
            this.response.appendString(look);
        }
        else
        {
            this.response.appendString(pet.getPetData().getType() + " " + pet.getRace() + " " + pet.getColor() + " " + ((pet instanceof HorsePet ? (((HorsePet) pet).hasSaddle() ? "3" : "2") + " 2 " + ((HorsePet) pet).getHairStyle() + " " + ((HorsePet) pet).getHairColor() + " 3 " + ((HorsePet) pet).getHairStyle() + " " + ((HorsePet) pet).getHairColor() + (((HorsePet) pet).hasSaddle() ? " 4 9 0" : "") : pet instanceof MonsterplantPet ? (((MonsterplantPet) pet).look.isEmpty() ? "2 1 8 6 0 -1 -1" : ((MonsterplantPet) pet).look) : "2 2 -1 0 3 -1 0")));
        }
        this.response.appendInt32(pet.getRoomUnit().getId());
        this.response.appendInt32(pet.getRoomUnit().getX());
        this.response.appendInt32(pet.getRoomUnit().getY());
        this.response.appendString(pet.getRoomUnit().getZ() + "");
        this.response.appendInt32(0);
        this.response.appendInt32(2);
        this.response.appendInt32(pet.getPetData().getType());
        this.response.appendInt32(pet.getUserId());
        this.response.appendString(""); //TODO Owner name
        this.response.appendInt32(pet instanceof MonsterplantPet ? 0 : 1);
        this.response.appendBoolean(pet instanceof HorsePet && ((HorsePet) pet).hasSaddle());
        this.response.appendBoolean(true);
        this.response.appendBoolean(true); //Can toggle breeding permissions.
        this.response.appendBoolean(true);
        this.response.appendBoolean(true); //Can treat?
        this.response.appendBoolean(true); //Can breed
        this.response.appendInt32(pet.getLevel());
        this.response.appendString("");
        pet.getRoomUnit().getStatus().put("grw", "0");

        return true;
    }
}
