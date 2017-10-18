package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionGuildFurni extends HabboItem
{
    private int guildId;

    public InteractionGuildFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.guildId = set.getInt("guild_id");
    }

    public InteractionGuildFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.guildId = 0;
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guildId);

        if(guild != null)
        {
            serverMessage.appendInt(2 + (this.isLimited() ? 256 : 0));
            serverMessage.appendInt(5);
            serverMessage.appendString(this.getExtradata());
            serverMessage.appendString(guild.getId() + "");
            serverMessage.appendString(guild.getBadge());
            serverMessage.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorOne()).valueA);
            serverMessage.appendString(Emulator.getGameEnvironment().getGuildManager().getBackgroundColor(guild.getColorTwo()).valueA);

            super.serializeExtradata(serverMessage);
        }
        else
        {
            serverMessage.appendInt((this.isLimited() ? 256 : 0));
            serverMessage.appendString(this.getExtradata());

            if(this.isLimited())
            {
                serverMessage.appendInt(10);
                serverMessage.appendInt(100);
            }
        }
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (objects.length > 0)
        {
            if (objects[0] instanceof Integer && room != null)
            {
                if(this.getExtradata().length() == 0)
                    this.setExtradata("0");

                if(this.getBaseItem().getStateCount() > 1)
                {
                    this.setExtradata("" + (Integer.valueOf(this.getExtradata()) + 1) % this.getBaseItem().getStateCount());
                    this.needsUpdate(true);
                    room.updateItemState(this);
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
    }

    public int getGuildId()
    {
        return this.guildId;
    }

    public void setGuildId(int guildId)
    {
        this.guildId = guildId;
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }
}
