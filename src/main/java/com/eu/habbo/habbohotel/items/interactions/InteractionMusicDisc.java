package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxMySongsComposer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionMusicDisc extends HabboItem
{
    private int songId;
    private boolean inQueue;

    public InteractionMusicDisc(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        String[] stuff = this.getExtradata().split("\n");

        if(stuff.length >= 7)
        {
            this.songId = Integer.valueOf(stuff[6]);
        }
    }

    public InteractionMusicDisc(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);

        String[] stuff = this.getExtradata().split("\n");

        if(stuff.length >= 7)
        {
            this.songId = Integer.valueOf(stuff[6]);
        }
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    public int getSongId()
    {
        return songId;
    }

    @Override
    public void onPlace(Room room)
    {
        super.onPlace(room);

        room.sendComposer(new JukeBoxMySongsComposer(room.getTraxManager().myList()).compose());
    }

    @Override
    public void onPickUp(Room room)
    {
        super.onPickUp(room);

        room.getTraxManager().removeSong(this.getId());
    }

    public boolean inQueue()
    {
        return this.inQueue;
    }

    public void inQueue(boolean inQueue)
    {
        this.inQueue = inQueue;
    }
}
