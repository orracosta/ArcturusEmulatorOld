package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredGiveRewardItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:31.
 */
public class WiredEffectGiveReward extends InteractionWiredEffect
{
    public final static WiredEffectType type = WiredEffectType.GIVE_REWARD;

    public int limit;
    public int given;
    public int rewardTime;
    public boolean uniqueRewards;

    public THashSet<WiredGiveRewardItem> rewardItems = new THashSet<WiredGiveRewardItem>();

    public WiredEffectGiveReward(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectGiveReward(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        return habbo != null && WiredHandler.getReward(habbo, this);
    }

    @Override
    public String getWiredData()
    {
        String data = limit + ":" + given + ":"+ rewardTime + ":" + (uniqueRewards ? 1 : 0) + ":";

        if(this.rewardItems.isEmpty())
        {
            data += "\t";
        }
        else
        {
            for (WiredGiveRewardItem item : this.rewardItems)
            {
                data += item.toString() + ";";
            }
        }

        return data;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(":");
        if(data.length > 0)
        {
            this.limit = Integer.valueOf(data[0]);
            this.given = Integer.valueOf(data[1]);
            this.rewardTime = Integer.valueOf(data[2]);
            this.uniqueRewards = data[3].equals("1");

            if(data.length > 4)
            {
                if(!data[4].equalsIgnoreCase("\t"))
                {
                    String[] items = data[4].split(";");

                    this.rewardItems.clear();

                    for (String s : items)
                    {
                        this.rewardItems.add(new WiredGiveRewardItem(s));
                    }
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.limit = 0;
        this.given = 0;
        this.rewardTime = 0;
        this.uniqueRewards = false;
        this.rewardItems.clear();
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message)
    {
        message.appendBoolean(false);
        message.appendInt32(this.rewardItems.size());
        message.appendInt32(0);
        message.appendInt32(this.getBaseItem().getSpriteId());
        message.appendInt32(this.getId());
        String s = "";

        for(WiredGiveRewardItem item : this.rewardItems)
        {
            s += item.wiredString() + ";";
        }
        message.appendString(s);
        message.appendInt32(3);
            message.appendInt32(this.rewardTime);
            message.appendInt32(this.uniqueRewards);
            message.appendInt32(this.limit);
        message.appendInt32(this.limit > 0);
        message.appendInt32(this.getType().code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.rewardTime = packet.readInt();
        this.uniqueRewards = packet.readInt() == 1;
        this.limit = packet.readInt();
        this.given = 0;

        String data = packet.readString();

        String[] items = data.split(";");

        this.rewardItems.clear();

        int i = 1;
        for(String s : items)
        {
            String[] d = s.split(",");

            if(d.length == 3)
            {
                this.rewardItems.add(new WiredGiveRewardItem(i, d[0].equalsIgnoreCase("0"), d[1], Integer.valueOf(d[2])));
            }
            else
            {
                Emulator.getLogging().logErrorLine("Incorrect WiredEffectGiveReward data: " + s);
            }
        }

        WiredHandler.dropRewards(this.getId());

        return true;
    }
}
