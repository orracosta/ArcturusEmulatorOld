package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.HabboItemNewState;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionObstacle extends HabboItem
{
    public InteractionObstacle(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionObstacle(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        AbstractPet pet = room.getPet(roomUnit);

        if (pet != null && pet instanceof HorsePet)
        {
            HorsePet horsePet = (HorsePet)pet;

            if (horsePet == null)
            {
                Habbo habbo = room.getHabbo(roomUnit);

                if (habbo != null && habbo.getHabboInfo().getRiding() != null)
                {
                    return true;
                }

                return false;
            }
            else
                return horsePet.getRider() != null;
        }

        return false;
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
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
        {
            AbstractPet pet = room.getPet(roomUnit);

            if(pet != null && pet instanceof HorsePet && ((HorsePet) pet).getRider() != null)
            {
                if (((HorsePet) pet).getTask() != null && ((HorsePet) pet).getTask().equals(PetTasks.RIDE))
                {
                    if (pet.getRoomUnit().getStatus().containsKey("jmp"))
                    {
                        pet.getRoomUnit().getStatus().remove("jmp");
                        Emulator.getThreading().run(new HabboItemNewState(this, room, "0"), 2000);
                    } else
                    {
                        int state = 0;
                        for (int i = 0; i < 2; i++)
                        {
                            state = Emulator.getRandom().nextInt(4) + 1;

                            if (state == 4)
                                break;
                        }

                        this.setExtradata(state + "");
                        pet.getRoomUnit().getStatus().put("jmp", "0");

                        AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("HorseConsecutiveJumpsCount"));
                        AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("HorseJumping"));
                    }

                    room.updateItemState(this);
                }
            }
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
        {
            AbstractPet pet = room.getPet(roomUnit);

            if(pet != null && pet instanceof HorsePet && ((HorsePet) pet).getRider() != null)
            {
                if (roomUnit.getBodyRotation().getValue() % 2 == 0)
                {
                    if (this.getRotation() == 2)
                    {
                        if(roomUnit.getBodyRotation().equals(RoomUserRotation.WEST))
                        {
                            ((HorsePet) pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile((short) (roomUnit.getX() - 3), roomUnit.getY()));
                        }
                        else if(roomUnit.getBodyRotation().equals(RoomUserRotation.EAST))
                        {
                            ((HorsePet) pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile((short) (roomUnit.getX() + 3), roomUnit.getY()));
                        }
                    }
                    else if(this.getRotation() == 4)
                    {
                        if(roomUnit.getBodyRotation().equals(RoomUserRotation.NORTH))
                        {
                            ((HorsePet) pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile(roomUnit.getX(), (short) (roomUnit.getY() - 3)));
                        }
                        else if(roomUnit.getBodyRotation().equals(RoomUserRotation.SOUTH))
                        {
                            ((HorsePet) pet).getRider().getRoomUnit().setGoalLocation(room.getLayout().getTile(roomUnit.getX(), (short) (roomUnit.getY() + 3)));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);

        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
        {
            AbstractPet pet = room.getPet(roomUnit);

            if(pet != null && pet instanceof HorsePet && ((HorsePet) pet).getRider() != null)
            {
                pet.getRoomUnit().getStatus().remove("jmp");
            }
        }
    }
}
