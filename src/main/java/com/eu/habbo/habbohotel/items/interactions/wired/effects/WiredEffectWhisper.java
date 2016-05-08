package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13-12-2014 20:15.
 */
public class WiredEffectWhisper extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.SHOW_MESSAGE;

    private String message = "";

    public WiredEffectWhisper(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectWhisper(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
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
        message.appendString(this.message);
        message.appendInt32(0);
        message.appendInt32(0);
        message.appendInt32(type.code);
        message.appendInt32(0);
        message.appendInt32(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();

        this.message = Emulator.getGameEnvironment().getWordFilter().filter(packet.readString(), null);

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(this.message.length() > 0)
        {
            if(roomUnit != null)
            {
                Habbo habbo = room.getHabbo(roomUnit);

                if (habbo != null)
                {
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(message.replace("%user%", habbo.getHabboInfo().getUsername()), habbo, habbo, RoomChatMessageBubbles.WIRED)));
                    return true;
                }
            }
            else
            {
                for(Habbo h : room.getCurrentHabbos().valueCollection())
                {
                    h.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(message.replace("%user%", h.getHabboInfo().getUsername()), h, h, RoomChatMessageBubbles.WIRED)));
                }
            }
        }
        return false;
    }

    @Override
    public String getWiredData()
    {
        return this.getDelay() + "\t" + this.message;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String wireData = set.getString("wired_data");
        this.message = "";

        if(wireData.split("\t").length >= 2)
        {
            super.setDelay(Integer.valueOf(wireData.split("\t")[0]));
            this.message = wireData.split("\t")[1];
        }
    }

    @Override
    public void onPickUp()
    {
        this.message = "";
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }
}
