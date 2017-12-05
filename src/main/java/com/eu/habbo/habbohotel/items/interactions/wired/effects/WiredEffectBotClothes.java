package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class WiredEffectBotClothes extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.BOT_CLOTHES;

    private String botName = "";
    private String botLook = "";

    public WiredEffectBotClothes(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectBotClothes(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        message.appendBoolean(false);
        message.appendInt(5);
        message.appendInt(0);
        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString(this.botName + ((char) 9) + "" + this.botLook);
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

        String[] data = packet.readString().split(((char) 9 ) + "");

        if(data.length == 2)
        {
            this.botName = data[0];
            this.botLook = data[1];
        }

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
        List<Bot> bots = room.getBots(this.botName);
        for(Bot bot : bots)
        {
            bot.setFigure(this.botLook);
        }

        return true;
    }

    @Override
    protected String getWiredData()
    {
        return this.getDelay() + "" + ((char) 9) + "" +
                this.botName   + ((char) 9) +
                this.botLook;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(((char) 9) + "");

        if(data.length >= 3)
        {
            this.setDelay(Integer.valueOf(data[0]));
            this.botName = data[1];
            this.botLook = data[2];
        }
    }

    @Override
    public void onPickUp()
    {
        this.botLook = "";
        this.botName = "";
        this.setDelay(0);
    }

    public String getBotName()
    {
        return botName;
    }

    public void setBotName(String botName)
    {
        this.botName = botName;
    }

    public String getBotLook()
    {
        return botLook;
    }

    public void setBotLook(String botLook)
    {
        this.botLook = botLook;
    }
}
