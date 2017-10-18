package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItem;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFXBox extends InteractionDefault
{
    public InteractionFXBox(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionFXBox(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (client != null && room.hasRights(client.getHabbo()))
        {
            if (client.getHabbo().getHabboInfo().getGender().equals(HabboGender.M))
            {
                if (this.getBaseItem().getEffectM() > 0)
                {
                    room.giveEffect(client.getHabbo(), this.getBaseItem().getEffectM());
                }
            }

            if (client.getHabbo().getHabboInfo().getGender().equals(HabboGender.F))
            {
                if (this.getBaseItem().getEffectF() > 0)
                {
                    room.giveEffect(client.getHabbo(), this.getBaseItem().getEffectF());
                }
            }

            this.setExtradata("1");
            room.updateItemState(this);
            room.removeHabboItem(this);
            HabboItem item = this;
            Emulator.getThreading().run(new Runnable()
            {
                @Override
                public void run()
                {
                    new QueryDeleteHabboItem(item).run();
                    room.sendComposer(new RemoveFloorItemComposer(item).compose());
                }
            }, 500);
        }
    }
}