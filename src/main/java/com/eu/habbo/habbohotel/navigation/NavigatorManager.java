package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class NavigatorManager
{
    public final THashMap<Integer, NavigatorPublicCategory> publicCategories = new THashMap<Integer, NavigatorPublicCategory>();
    public final THashMap<String, Map.Entry<Method, NavigatorFilterComparator>> filterSettings = new THashMap<String, Map.Entry<Method, NavigatorFilterComparator>>();
    public final THashMap<String, NavigatorFilter> filters = new THashMap<String, NavigatorFilter>();

    public NavigatorManager()
    {
        long millis = System.currentTimeMillis();
        loadNavigator();

        filters.put(NavigatorPublicFilter.name, new NavigatorPublicFilter());
        filters.put(NavigatorHotelFilter.name, new NavigatorHotelFilter());
        filters.put(NavigatorRoomAdsFilter.name, new NavigatorRoomAdsFilter());
        filters.put(NavigatorUserFilter.name, new NavigatorUserFilter());

        Emulator.getLogging().logStart("Navigator Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void loadNavigator()
    {
        synchronized (this.publicCategories)
        {
            this.publicCategories.clear();

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM navigator_publiccats WHERE visible = '1'");
                ResultSet set = statement.executeQuery();

                while(set.next())
                {
                    this.publicCategories.put(set.getInt("id"), new NavigatorPublicCategory(set));
                }

                set.close();
                statement.getConnection().close();
                statement.close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM navigator_publics WHERE visible = '1'");
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    NavigatorPublicCategory category = this.publicCategories.get(set.getInt("public_cat_id"));

                    if (category != null)
                    {
                        category.addRoom(Emulator.getGameEnvironment().getRoomManager().loadRoom(set.getInt("room_id")));
                    }
                }

                set.close();
                statement.getConnection().close();
                statement.close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        synchronized (this.filterSettings)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM navigator_filter");
                ResultSet set = statement.executeQuery();

                while(set.next())
                {
                    Method field = null;
                    Class clazz = Room.class;

                    if (set.getString("field").contains("."))
                    {
                        for (String s : (set.getString("field")).split("."))
                        {
                            try
                            {
                                field = clazz.getDeclaredMethod(s);
                                clazz = field.getReturnType();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                break;
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            field = clazz.getDeclaredMethod(set.getString("field"));
                            clazz = field.getReturnType();
                        }
                        catch (Exception e)
                        {
                            continue;
                        }
                    }

                    if (field != null)
                    {
                        this.filterSettings.put(set.getString("key"), new AbstractMap.SimpleEntry<Method, NavigatorFilterComparator>(field, NavigatorFilterComparator.valueOf(set.getString("compare").toUpperCase())));
                    }
                }

                set.close();
                statement.getConnection().close();
                statement.close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public NavigatorFilterComparator comperatorForField(Method field)
    {
        synchronized (this.filterSettings)
        {
            for (Map.Entry<String, Map.Entry<Method, NavigatorFilterComparator>> set : this.filterSettings.entrySet())
            {
                if (set.getValue().getKey() == field)
                {
                    return set.getValue().getValue();
                }
            }
        }

        return null;
    }
}
