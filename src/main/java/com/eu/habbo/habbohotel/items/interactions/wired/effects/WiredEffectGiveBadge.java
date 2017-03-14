package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.WiredRewardAlertComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredEffectGiveBadge extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    private String badge = "";

    public WiredEffectGiveBadge(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectGiveBadge(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        message.appendBoolean(false);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        message.appendString(this.badge);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(type.code);
        message.appendInt32(this.getDelay());
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.badge = packet.readString();

        packet.readInt();
        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo == null)
            return false;

        if (this.badge.isEmpty())
            return false;
        
        if (habbo.getHabboInventory().getBadgesComponent().hasBadge(this.badge))
        {
            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_ALREADY_RECEIVED));
        }
        else
        {
            BadgesComponent.createBadge(this.badge, habbo);
            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_RECEIVED_BADGE));
        }

        return true;
    }

    @Override
    protected String getWiredData()
    {
        return this.getDelay() + "\t" + this.badge;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String wireData = set.getString("wired_data");
        String[] data = wireData.split("\t");

        if(data.length >= 2)
        {
            super.setDelay(Integer.valueOf(data[0]));
            this.badge = data[1];
        }
    }

    @Override
    public void onPickUp()
    {
        this.badge = "";
        this.setDelay(0);
    }
}

