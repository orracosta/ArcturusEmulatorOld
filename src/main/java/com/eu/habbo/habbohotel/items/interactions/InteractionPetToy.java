package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.PetClearPosture;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionPetToy extends HabboItem
{
    public InteractionPetToy(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionPetToy(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
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
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onWalkOn(RoomUnit client, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(client, room, objects);

        AbstractPet pet = room.getPet(client);

        if(pet != null && pet instanceof Pet)
        {
            if (pet.getEnergy() <= 35)
            {
                return;
            }

            ((Pet) pet).setTask(PetTasks.PLAY);
            pet.getRoomUnit().setGoalLocation(room.getLayout().getTile(this.getX(), this.getY()));
            pet.getRoomUnit().setRotation(RoomUserRotation.values()[this.getRotation()]);
            pet.getRoomUnit().getStatus().clear();
            pet.getRoomUnit().getStatus().remove("mv");
            pet.getRoomUnit().getStatus().put("pla", "0");
            ((Pet) pet).packetUpdate = true;
            HabboItem item = this;
            Emulator.getThreading().run(new Runnable()
            {
                @Override
                public void run()
                {
                    pet.addHappyness(25);
                    item.setExtradata("0");
                    room.updateItem(item);
                    new PetClearPosture((Pet)pet, "pla", null, true).run();;
                }
            }, 2500 + (Emulator.getRandom().nextInt(20) * 500));
            this.setExtradata("1");
            room.updateItemState(this);
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);

        AbstractPet pet = room.getPet(roomUnit);

        if (pet != null && pet instanceof Pet)
        {
            this.setExtradata("0");
            room.updateItemState(this);
        }
    }

    @Override
    public boolean allowWiredResetState()
    {
        return false;
    }
}