package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.pets.breeding.PetBreedingResultComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetBreedingNest extends HabboItem
{
    public AbstractPet petOne = null;
    public AbstractPet petTwo = null;

    public InteractionPetBreedingNest(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPetBreedingNest(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return room.getPet(roomUnit) != null && !this.boxFull();
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }


    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        AbstractPet pet = room.getPet(roomUnit);

        if (pet != null)
        {
            if (!boxFull())
            {
                this.addPet(pet);

                if (boxFull())
                {
                    Habbo ownerPetOne = room.getHabbo(petOne.getUserId());
                    Habbo ownerPetTwo = room.getHabbo(petTwo.getUserId());

                    if (ownerPetOne != null && ownerPetTwo != null)
                    {
                        ownerPetTwo.getClient().sendResponse(new PetBreedingResultComposer(0, this.petOne, ownerPetOne.getHabboInfo().getUsername(), this.petTwo, ownerPetTwo.getHabboInfo().getUsername()));
                    }
                }
            }
        }
    }

    public boolean addPet(AbstractPet pet)
    {
        if (this.petOne == null)
        {
            this.petOne = pet;
            return true;
        }
        else if (this.petTwo == null && this.petOne != pet)
        {
            this.petTwo = pet;
            return true;
        }

        return false;
    }

    public boolean boxFull()
    {
        return this.petOne != null && this.petTwo != null;
    }

    @Override
    public boolean allowWiredResetState()
    {
        return false;
    }
}