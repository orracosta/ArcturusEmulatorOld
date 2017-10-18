package com.eu.habbo.habbohotel.items.interactions.wired.conditions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredConditionHabboWearsBadge extends InteractionWiredCondition
{
    public static final WiredConditionType type = WiredConditionType.ACTOR_WEARS_BADGE;

    private String badge = "";

    public WiredConditionHabboWearsBadge(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredConditionHabboWearsBadge(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            synchronized (habbo.getInventory().getBadgesComponent().getWearingBadges())
            {
                for (HabboBadge badge : habbo.getInventory().getBadgesComponent().getWearingBadges())
                {
                    if (badge.getCode().equalsIgnoreCase(this.badge))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.badge;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.badge = set.getString("wired_data");
    }

    @Override
    public void onPickUp()
    {
        this.badge = "";
    }

    @Override
    public WiredConditionType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.badge);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.badge = packet.readString();

        return true;
    }
}
