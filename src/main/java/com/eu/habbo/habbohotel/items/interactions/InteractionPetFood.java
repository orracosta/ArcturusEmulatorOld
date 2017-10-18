package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.PetEatAction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetFood extends HabboItem
{
    public InteractionPetFood(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPetFood(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit client, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(client, room, objects);

        if(this.getExtradata().length() == 0)
            this.setExtradata("0");

        AbstractPet pet = room.getPet(client);

        if(pet != null && pet instanceof Pet)
        {
            if(pet.getPetData().haveFoodItem(this))
            {
                if (((Pet) pet).levelHunger >= 35)
                {
                    ((Pet) pet).setTask(PetTasks.EAT);
                    pet.getRoomUnit().setGoalLocation(room.getLayout().getTile(this.getX(), this.getY()));
                    pet.getRoomUnit().setRotation(RoomUserRotation.values()[this.getRotation()]);
                    pet.getRoomUnit().getStatus().clear();
                    pet.getRoomUnit().getStatus().remove("mv");
                    pet.getRoomUnit().getStatus().put("eat", "0");
                    room.sendComposer(new RoomUserStatusComposer(client).compose());
                    Emulator.getThreading().run(new PetEatAction((Pet) pet, this));
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
    }

    @Override
    public boolean allowWiredResetState()
    {
        return false;
    }
}
