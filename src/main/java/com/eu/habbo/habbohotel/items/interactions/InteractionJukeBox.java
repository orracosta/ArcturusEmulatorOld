package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.list.array.TIntArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionJukeBox extends HabboItem
{
    private int currentItem = 0;
    private int currentIndex = 0;
    private long startTime = 0;
    private TIntArrayList musicDisks = new TIntArrayList();

    public InteractionJukeBox(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);

        String[] data = set.getString("extra_data").split("\t");

        if (data.length >= 2)
        {
            this.currentItem = Integer.valueOf(data[0]);
            this.startTime = Integer.valueOf(data[1]);

            if (data.length > 2)
            {
                int itemsCount = Integer.valueOf(data[2]);

                if (data.length == itemsCount + 3)
                {
                    for (int i = 3; i < data.length; i++)
                    {
                        this.musicDisks.add(Integer.valueOf(data[i]));
                    }
                }
            }
        }
    }

    public InteractionJukeBox(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.currentItem > 0 ? "1" : "0");

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

    @Override
    public void onPickUp(Room room)
    {
        super.onPickUp(room);

        this.startTime = 0;
        this.currentItem = 0;
        this.musicDisks.clear();
    }

    @Override
    public String getExtradata()
    {
        String data = this.currentItem + "\t" + this.startTime + "\t" + this.musicDisks.size();

        for (Integer i : this.musicDisks.toArray())
        {
            data += "\t" + i;
        }

        return data;
    }

    public boolean hasItemInPlaylist(int itemId)
    {
        return this.musicDisks.contains(itemId);
    }

    public void addToPlayList(int itemId, Room room)
    {
        this.musicDisks.add(itemId);
    }

    public boolean removeFromPlayList(int itemId, Room room)
    {
        this.musicDisks.remove(itemId);

        if (this.currentItem == itemId)
        {
            if (this.currentIndex >= this.musicDisks.size())
            {
                this.currentIndex = 0;
                this.currentIndex = 0;
            }


            if (this.currentIndex < this.musicDisks.size())
            {
                this.currentItem = this.musicDisks.get(this.currentIndex);
            }

            room.updateItem(this);

            return true;
        }

        return false;
    }

    public void nextSong()
    {
        this.currentIndex++;
        this.currentIndex = this.currentIndex % this.getMusicDisks().size();

        if (this.currentIndex < this.getMusicDisks().size())
        {
            this.currentItem = this.musicDisks.get(this.currentIndex);
            this.startTime = System.currentTimeMillis();
        }
        else
        {
            this.currentIndex = 0;
            this.currentItem = 0;
        }
    }

    public int getCurrentItem()
    {
        return currentItem;
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    public long timePlayed()
    {
        return System.currentTimeMillis() - this.startTime;
    }

    public TIntArrayList getMusicDisks()
    {
        return musicDisks;
    }
}