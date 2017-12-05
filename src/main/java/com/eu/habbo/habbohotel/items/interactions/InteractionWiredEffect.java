package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.outgoing.wired.WiredEffectDataComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionWiredEffect extends InteractionWired
{
    private int delay;

    public InteractionWiredEffect(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionWiredEffect(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if (client != null)
        {
            if (room.hasRights(client.getHabbo()))
            {
                client.sendResponse(new WiredEffectDataComposer(this, room));
                this.activateBox(room);
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

    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    public abstract boolean saveData(ClientMessage packet, GameClient gameClient);

    /**
     * Sets the delay of execution.
     * @param value The delay of execution.
     */
    protected void setDelay(int value)
    {
        this.delay = value;
    }

    /**
     * @return The delay of execution.
     */
    public int getDelay()
    {
        return this.delay;
    }

    /**
     * @return The type of the wired trigger.
     */
    public abstract WiredEffectType getType();

    /**
     * @return Returns true if the InteractionWiredEffect can only be executed
     * if there is a RoomUnit triggering the wired.
     */
    public boolean requiresTriggeringUser()
    {
        return false;
    }
}
