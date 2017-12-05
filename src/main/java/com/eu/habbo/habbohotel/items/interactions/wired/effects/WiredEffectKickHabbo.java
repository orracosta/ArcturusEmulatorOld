package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.threading.runnables.RoomUnitKick;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectKickHabbo extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.KICK_USER;

    private String message = "";

    public WiredEffectKickHabbo(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectKickHabbo(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(room == null)
            return false;

        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            if(!habbo.hasPermission("acc_unkickable") && habbo.getHabboInfo().getId() != room.getOwnerId())
            {
                room.giveEffect(habbo, 4);

                if(!this.message.isEmpty())
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(this.message, habbo, habbo, RoomChatMessageBubbles.ALERT)));

                Emulator.getThreading().run(new RoomUnitKick(habbo, room, true), 2000);

                return true;
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
        try
        {
            String[] data = set.getString("wired_data").split("\t");

            if (data.length >= 1)
            {
                this.setDelay(Integer.valueOf(data[0]));

                if (data.length >= 2)
                {
                    this.message = data[1];
                }
            }
        }
        catch (Exception e)
        {
            this.message = "";
            this.setDelay(0);
        }
    }

    @Override
    public void onPickUp()
    {
        this.message = "";
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
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
        message.appendString(this.message);
        message.appendInt(0);
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
        this.message = packet.readString();
        packet.readInt();
        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public boolean requiresTriggeringUser()
    {
        return true;
    }
}
