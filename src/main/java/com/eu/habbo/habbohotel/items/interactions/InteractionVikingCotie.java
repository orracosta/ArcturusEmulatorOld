package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.threading.runnables.VikingCotieBurnDown;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 16-10-2015 22:27.
 */
public class InteractionVikingCotie extends InteractionDefault
{
    public InteractionVikingCotie(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionVikingCotie(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if(this.getExtradata().isEmpty() || this.getExtradata().equalsIgnoreCase("0"))
        {
            if (client.getHabbo().getRoomUnit().getEffectId() == 5)
            {
                this.setExtradata("1");

                Emulator.getThreading().run(new VikingCotieBurnDown(this, room, client.getHabbo()), 500);
                room.updateItem(this);
            }
        }

        super.onClick(client, room, objects);
    }
}
