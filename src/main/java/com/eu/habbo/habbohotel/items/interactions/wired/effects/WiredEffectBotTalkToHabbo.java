package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectBotTalkToHabbo extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.BOT_TALK_TO_AVATAR;

    private int mode;
    private String botName = "";
    private String message = "";

    public WiredEffectBotTalkToHabbo(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectBotTalkToHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        message.appendString(this.botName + "" + ((char) 9) + "" + this.message);
        message.appendInt(1);
        message.appendInt(this.mode);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());

        if (this.requiresTriggeringUser())
        {
            List<Integer> invalidTriggers = new ArrayList<>();
            room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>()
            {
                @Override
                public boolean execute(InteractionWiredTrigger object)
                {
                    if (!object.isTriggeredByRoomUnit())
                    {
                        invalidTriggers.add(object.getBaseItem().getSpriteId());
                    }
                    return true;
                }
            });
            message.appendInt(invalidTriggers.size());
            for (Integer i : invalidTriggers)
            {
                message.appendInt(i);
            }
        }
        else
        {
            message.appendInt(0);
        }
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();

        this.mode = packet.readInt();
        String[] data = packet.readString().split("" + ((char) 9));

        if(data.length == 2)
        {
            this.botName = data[0];
            this.message = data[1];
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
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            String m = this.message;
            m = m.replace(Emulator.getTexts().getValue("wired.variable.username"), habbo.getHabboInfo().getUsername())
                    .replace(Emulator.getTexts().getValue("wired.variable.credits"), habbo.getHabboInfo().getCredits() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.pixels"), habbo.getHabboInfo().getPixels() + "")
                    .replace(Emulator.getTexts().getValue("wired.variable.points"), habbo.getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type")) + "");

            List<Bot> bots = room.getBots(this.botName);

            for(Bot bot : bots)
            {
                if(this.mode == 1)
                {
                    bot.whisper(m, habbo);
                }
                else
                {
                    bot.talk(habbo.getHabboInfo().getUsername() + ": " + m);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    protected String getWiredData()
    {
        return this.getDelay() + "" + ((char) 9) + "" + this.mode + "" + ((char) 9) + "" + this.botName + "" + ((char) 9 ) + "" + this.message;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split(((char) 9) + "");

        if(data.length == 4)
        {
            this.setDelay(Integer.valueOf(data[0]));
            this.mode = data[1].equalsIgnoreCase("1") ? 1 : 0;
            this.botName = data[2];
            this.message = data[3];
        }
    }

    @Override
    public void onPickUp()
    {
        this.botName = "";
        this.message = "";
        this.mode = 0;
        this.setDelay(0);
    }

    @Override
    public boolean requiresTriggeringUser()
    {
        return true;
    }
}
