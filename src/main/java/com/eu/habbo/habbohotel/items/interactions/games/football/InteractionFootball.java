package com.eu.habbo.habbohotel.items.interactions.games.football;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionPushable;
import com.eu.habbo.habbohotel.rooms.Room;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFootball extends InteractionPushable
{
    public InteractionFootball(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionFootball(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    protected void cycle(final Room room)
    {
        super.cycle(room);
    }

    protected void calcState()
    {
        if(this.velocity > 0)
        {
            this.setExtradata(velocity + "");
        }
    }
}
