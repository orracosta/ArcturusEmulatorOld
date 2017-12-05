package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerHabboSaysCommand  extends InteractionWiredTrigger
{
    private static final WiredTriggerType type = WiredTriggerType.SAY_COMMAND;

    private boolean ownerOnly = false;
    private String key = "";

    public WiredTriggerHabboSaysCommand(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredTriggerHabboSaysCommand(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        Habbo habbo = room.getHabbo(roomUnit);

        if(habbo != null)
        {
            if (this.key.length() > 0)
            {
                if (stuff[0] instanceof String)
                {
                    if (((String) stuff[0]).equalsIgnoreCase(this.key))
                    {
                        if (this.ownerOnly && room.getOwnerId() != habbo.getHabboInfo().getId())
                            return false;

                        habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(this.key, habbo, habbo, RoomChatMessageBubbles.ALERT)));

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
        return (this.ownerOnly ? "1" : "0") + "\t" + this.key;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        String[] data = set.getString("wired_data").split("\t");

        if(data.length == 2)
        {
            this.ownerOnly = data[0].equalsIgnoreCase("1");
            this.key = data[1];
        }
    }

    @Override
    public void onPickUp()
    {
        this.ownerOnly = false;
        this.key = "";
    }

    @Override
    public WiredTriggerType getType()
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
        message.appendString(this.key);
        message.appendInt(0);
        message.appendInt(1);
        message.appendInt(this.getType().code);
        message.appendInt(0);
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet)
    {
        packet.readInt();
        this.ownerOnly = packet.readInt() == 1;
        this.key = packet.readString();

        return true;
    }

    @Override
    public boolean isTriggeredByRoomUnit()
    {
        return true;
    }
}
