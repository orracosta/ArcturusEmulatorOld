package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionDefault extends HabboItem
{
    public InteractionDefault(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionDefault(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
    public boolean isWalkable()
    {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(room != null && (client == null || canToggle(client.getHabbo(), room) || (objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE)))
        {
            super.onClick(client, room, objects);

            if (objects != null && objects.length > 0)
            {
                if (objects[0] instanceof Integer)
                {
                    if (this.getExtradata().length() == 0)
                        this.setExtradata("0");

                    if (this.getBaseItem().getStateCount() > 0)
                    {
                        int currentState = 0;

                        try
                        {
                            currentState = Integer.valueOf(this.getExtradata());
                        }
                        catch (NumberFormatException e)
                        {
                            Emulator.getLogging().logErrorLine("Incorrect extradata (" + this.getExtradata() + ") for item ID (" + this.getId() + ") of type (" + this.getBaseItem().getName() + ")");
                        }

                        this.setExtradata("" + (currentState + 1) % this.getBaseItem().getStateCount());
                        this.needsUpdate(true);

                        room.updateItemState(this);
                    }
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        if (roomUnit != null)
        {
            if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)
            {
                if (roomUnit.getRoomUnitType().equals(RoomUnitType.USER))
                {
                    Habbo habbo = room.getHabbo(roomUnit);

                    if (habbo != null)
                    {
                        if (habbo.getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectM())
                        {
                            room.giveEffect(habbo, this.getBaseItem().getEffectM());
                            return;
                        }

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0 && habbo.getRoomUnit().getEffectId() != this.getBaseItem().getEffectF())
                        {
                            room.giveEffect(habbo, this.getBaseItem().getEffectF());
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);

        if (roomUnit != null)
        {
            if (this.getBaseItem().getEffectF() > 0 || this.getBaseItem().getEffectM() > 0)
            {
                if (roomUnit.getRoomUnitType().equals(RoomUnitType.USER))
                {
                    Habbo habbo = room.getHabbo(roomUnit);

                    if (habbo != null)
                    {
                        if (habbo.getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() > 0)
                        {
                            room.giveEffect(habbo, 0);
                            return;
                        }

                        if (habbo.getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() > 0)
                        {
                            room.giveEffect(habbo, 0);
                            return;
                        }
                    }
                }
            }
        }
    }

    public boolean canToggle(Habbo habbo, Room room)
    {
        return room.hasRights(habbo);
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }
}
