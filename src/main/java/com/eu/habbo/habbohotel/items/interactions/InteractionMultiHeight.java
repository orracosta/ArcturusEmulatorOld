package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMultiHeight extends HabboItem
{
    public InteractionMultiHeight(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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

    public InteractionMultiHeight(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    @Override
    public boolean isWalkable()
    {
        return this.getBaseItem().allowWalk() || this.getBaseItem().allowSit();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (client != null)
        {
            if (!room.hasRights(client.getHabbo()) && !(objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE))
                return;
        }

        if (objects.length > 0)
        {
            if (objects[0] instanceof Integer && room != null)
            {
                this.needsUpdate(true);

                if(this.getExtradata().length() == 0)
                    this.setExtradata("0");

                if(this.getBaseItem().getMultiHeights().length > 0)
                {
                    this.setExtradata("" + (Integer.valueOf(this.getExtradata()) + 1) % (this.getBaseItem().getMultiHeights().length));
                    this.needsUpdate(true);
                    room.updateItemState(this);
                    room.sendComposer(new UpdateStackHeightComposer(this.getX(), this.getY(), this.getBaseItem().getMultiHeights()[Integer.valueOf(this.getExtradata())] * 256.0D).compose());
                }

                if(this.isWalkable())
                {
                    THashSet<Habbo> habbos = room.getHabbosOnItem(this);
                    THashSet<RoomUnit> updatedUnits = new THashSet<RoomUnit>();
                    for (Habbo habbo : habbos)
                    {
                        if(habbo.getRoomUnit() == null)
                            continue;

                        if(habbo.getRoomUnit().getStatus().containsKey("mv"))
                            continue;

                        if (this.getBaseItem().getMultiHeights().length >= 0)
                        {
                            if (this.getBaseItem().allowSit())
                            {
                                habbo.getRoomUnit().getStatus().put("sit", this.getBaseItem().getMultiHeights()[(this.getExtradata().isEmpty() ? 0 : Integer.valueOf(this.getExtradata()) % (this.getBaseItem().getMultiHeights().length))] * 1.0D + "");
                            }
                            else
                            {
                                habbo.getRoomUnit().setZ(this.getZ() + this.getBaseItem().getMultiHeights()[(this.getExtradata().isEmpty() ? 0 : Integer.valueOf(this.getExtradata()) % (this.getBaseItem().getMultiHeights().length))]);
                            }
                        }

                        updatedUnits.add(habbo.getRoomUnit());
                    }
                    room.sendComposer(new RoomUserStatusComposer(updatedUnits, true).compose());
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects)
    {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
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
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
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

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }
}
