package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created on 26-8-2014 18:45.
 */
public class NavigatorManager
{
    private final THashMap<PublicRoom, THashSet<PublicRoom>> publicRooms;

    public NavigatorManager()
    {
        long millis = System.currentTimeMillis();
        this.publicRooms = new THashMap<PublicRoom, THashSet<PublicRoom>>();

        loadNavigator();

        Emulator.getLogging().logStart("Navigator Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void loadNavigator()
    {
        synchronized (this.publicRooms)
        {
            this.publicRooms.clear();

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM navigator_publics ORDER BY parent_id DESC");
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    PublicRoom room = new PublicRoom(set);

                    if (set.getInt("parent_id") == -1)
                    {
                        this.publicRooms.put(room, new THashSet<PublicRoom>());
                    }
                    else
                    {
                        PublicRoom parent = this.getParent(set.getInt("parent_id"));

                        if (parent != null)
                        {
                            this.publicRooms.get(parent).add(room);
                        }
                    }
                }
                set.close();
                statement.close();
                statement.getConnection().close();
            } catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public PublicRoom getParent(int parentId)
    {
        synchronized (this.publicRooms)
        {
            for (Map.Entry<PublicRoom, THashSet<PublicRoom>> map : this.publicRooms.entrySet())
            {
                if (map.getKey().parentId == parentId)
                    return map.getKey();
            }
        }

        return null;
    }

    public THashMap<PublicRoom, THashSet<PublicRoom>> getPublicRooms() {
        return publicRooms;
    }

    public void dispose()
    {
        synchronized (this.publicRooms)
        {
            this.publicRooms.clear();
        }

        Emulator.getLogging().logShutdownLine("NavigatorManager -> Disposed!");
    }
}
