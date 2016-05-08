package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToRoomUnit;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 18-9-2015 22:04.
 */
public class ButlerBot extends Bot
{
    public static THashMap<THashSet<String>, Integer> serveItems = new THashMap<THashSet<String>, Integer>();

    public ButlerBot(ResultSet set) throws SQLException
    {
        super(set);
    }

    public ButlerBot(Bot bot)
    {
        super(bot);
    }

    public static void initialise()
    {
        if(serveItems == null)
            serveItems = new THashMap<THashSet<String>, Integer>();

        serveItems.clear();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM bot_serves");
            ResultSet set = statement.executeQuery();

            while (set.next())
            {
                String[] keys = set.getString("keys").split(";");
                THashSet<String> ks = new THashSet<String>();
                for(String key : keys)
                {
                    ks.add(key);
                }
                serveItems.put(ks, set.getInt("item"));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public static void dispose()
    {
        serveItems.clear();
    }

    @Override
    public void onUserSay(final RoomChatMessage message)
    {
        if(this.getRoomUnit().isWalking())
            return;

        if(message.getMessage() != null)
        {
            if(message.getMessage().toLowerCase().startsWith(this.getName().toLowerCase()))
            {
                for(Map.Entry<THashSet<String>, Integer> set : serveItems.entrySet())
                {
                    for(String s : set.getKey())
                    {
                        if(message.getMessage().toLowerCase().contains(s))
                        {
                            List<Runnable> tasks = new ArrayList<Runnable>();
                            tasks.add(new RoomUnitGiveHanditem(message.getHabbo().getRoomUnit(), message.getHabbo().getHabboInfo().getCurrentRoom(), set.getValue()));
                            tasks.add(new RoomUnitGiveHanditem(this.getRoomUnit(), message.getHabbo().getHabboInfo().getCurrentRoom(), 0));
                            final String key = s;
                            final Bot b = this;
                            tasks.add(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    b.talk(Emulator.getTexts().getValue("bots.butler.given").replace("%key%", key).replace("%username%", message.getHabbo().getHabboInfo().getUsername()));
                                }
                            });
                            Emulator.getThreading().run(new RoomUnitGiveHanditem(this.getRoomUnit(), message.getHabbo().getHabboInfo().getCurrentRoom(), set.getValue()));
                            Emulator.getThreading().run(new RoomUnitWalkToRoomUnit(this.getRoomUnit(), message.getHabbo().getRoomUnit(), message.getHabbo().getHabboInfo().getCurrentRoom(),  tasks, null));
                            return;
                        }
                    }
                }
            }
        }
    }
}
