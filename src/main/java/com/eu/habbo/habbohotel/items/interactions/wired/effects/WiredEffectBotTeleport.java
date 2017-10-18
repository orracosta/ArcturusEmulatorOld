package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.RoomUnitTeleport;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WiredEffectBotTeleport extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.BOT_TELEPORT;

    private THashSet<HabboItem> items;
    private String botName = "";

    public WiredEffectBotTeleport(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredEffectBotTeleport(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.items)
        {
            if(item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                items.add(item);
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }

        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for(HabboItem item : this.items)
            message.appendInt(item.getId());

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.botName);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();
        this.botName = packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
        }

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
        if(this.items.isEmpty())
            return false;

        List<Bot> bots = room.getBots(this.botName);

        if(bots.isEmpty())
            return false;

        for(Bot bot : bots)
        {
            int i = Emulator.getRandom().nextInt(this.items.size()) + 1;
            int j = 1;
            for (HabboItem item : this.items)
            {
                if(item.getRoomId() != 0 && item.getRoomId() == bot.getRoom().getId())
                {
                    if (i == j)
                    {
                        int currentEffect = bot.getRoomUnit().getEffectId();

                        room.giveEffect(bot.getRoomUnit(), 4);
                        Emulator.getThreading().run(new RoomUnitTeleport(bot.getRoomUnit(), room, item.getX(), item.getY(), item.getZ() + (item.getBaseItem().allowSit() ? item.getBaseItem().getHeight() - 0.50 : 0D), currentEffect));
                        break;
                    } else
                    {
                        j++;
                    }
                }
            }
        }

        return true;
    }

    @Override
    protected String getWiredData()
    {
        String wiredData = this.getDelay() + "\t" + this.botName + ";";

        if(items != null && !items.isEmpty())
        {
            for (HabboItem item : this.items)
            {
                if(item.getRoomId() != 0)
                {
                    wiredData += item.getId() + ";";
                }
            }
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items = new THashSet<HabboItem>();
        String[] wiredData = set.getString("wired_data").split("\t");

        if (wiredData.length >= 2)
        {
            this.setDelay(Integer.valueOf(wiredData[0]));
            String[] data = wiredData[1].split(";");

            if (data.length > 1)
            {
                this.botName = data[0];

                for (int i = 1; i < data.length; i++)
                {
                    HabboItem item = room.getHabboItem(Integer.valueOf(data[i]));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.botName = "";
        this.items.clear();
        this.setDelay(0);
    }
}
