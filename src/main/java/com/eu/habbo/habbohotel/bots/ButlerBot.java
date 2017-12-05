package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.plugin.events.bots.BotServerItemEvent;
import com.eu.habbo.threading.runnables.RoomUnitGiveHanditem;
import com.eu.habbo.threading.runnables.RoomUnitWalkToRoomUnit;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ButlerBot extends Bot
{
    public static THashMap<THashSet<String>, Integer> serveItems = new THashMap<THashSet<String>, Integer>();

    /**
     * This class is a bot that will hand out handitems upon triggering with the correct keys.
     * @param set
     * @throws SQLException
     */
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

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM bot_serves"))
        {
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

        if (this.getRoomUnit().getCurrentLocation().distance(message.getHabbo().getRoomUnit().getCurrentLocation()) <= Emulator.getConfig().getInt("hotel.bot.butler.servedistance"))
        if(message.getUnfilteredMessage() != null)
        {
            for(Map.Entry<THashSet<String>, Integer> set : serveItems.entrySet())
            {
                for(String s : set.getKey())
                {
                    if(message.getUnfilteredMessage().toLowerCase().contains(s))
                    {
                        BotServerItemEvent serveEvent = new BotServerItemEvent(this, message.getHabbo(), set.getValue());
                        if (Emulator.getPluginManager().fireEvent(serveEvent).isCancelled())
                        {
                            return;
                        }

                        if (this.getRoomUnit().canWalk())
                        {
                            final String key = s;
                            final Bot b = this;

                            List<Runnable> tasks = new ArrayList<Runnable>();
                            tasks.add(new RoomUnitGiveHanditem(serveEvent.habbo.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), serveEvent.itemId));
                            tasks.add(new RoomUnitGiveHanditem(this.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), 0));

                            tasks.add(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    b.talk(Emulator.getTexts().getValue("bots.butler.given").replace("%key%", key).replace("%username%", serveEvent.habbo.getHabboInfo().getUsername()));
                                }
                            });
                            Emulator.getThreading().run(new RoomUnitGiveHanditem(this.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), serveEvent.itemId));
                            Emulator.getThreading().run(new RoomUnitWalkToRoomUnit(this.getRoomUnit(), serveEvent.habbo.getRoomUnit(), serveEvent.habbo.getHabboInfo().getCurrentRoom(), tasks, null));
                        }
                        else
                        {
                            this.getRoom().giveHandItem(serveEvent.habbo, serveEvent.itemId);
                            this.talk(Emulator.getTexts().getValue("bots.butler.given").replace("%key%", s).replace("%username%", serveEvent.habbo.getHabboInfo().getUsername()));
                        }
                        return;
                    }
                }
            }
        }
    }
}
