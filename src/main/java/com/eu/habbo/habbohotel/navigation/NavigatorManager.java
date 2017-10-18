package com.eu.habbo.habbohotel.navigation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import gnu.trove.map.hash.THashMap;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NavigatorManager
{
    public static int MAXIMUM_RESULTS_PER_PAGE = 10;

    public final THashMap<Integer, NavigatorPublicCategory> publicCategories = new THashMap<Integer, NavigatorPublicCategory>();
    public final ConcurrentHashMap<String, NavigatorFilterField> filterSettings = new ConcurrentHashMap<String, NavigatorFilterField>();
    public final THashMap<String, NavigatorFilter> filters = new THashMap<String, NavigatorFilter>();

    public NavigatorManager()
    {
        long millis = System.currentTimeMillis();
        loadNavigator();

        filters.put(NavigatorPublicFilter.name, new NavigatorPublicFilter());
        filters.put(NavigatorHotelFilter.name, new NavigatorHotelFilter());
        filters.put(NavigatorRoomAdsFilter.name, new NavigatorRoomAdsFilter());
        filters.put(NavigatorUserFilter.name, new NavigatorUserFilter());
        filters.put(NavigatorFavoriteFilter.name, new NavigatorFavoriteFilter());

        Emulator.getLogging().logStart("Navigator Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void loadNavigator()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            synchronized (this.publicCategories)
            {
                this.publicCategories.clear();

                try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM navigator_publiccats WHERE visible = '1'"))
                {
                    while(set.next())
                    {
                        this.publicCategories.put(set.getInt("id"), new NavigatorPublicCategory(set));
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }

                try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM navigator_publics WHERE visible = '1'"))
                {
                    while (set.next())
                    {
                        NavigatorPublicCategory category = this.publicCategories.get(set.getInt("public_cat_id"));

                        if (category != null)
                        {
                            category.addRoom(Emulator.getGameEnvironment().getRoomManager().loadRoom(set.getInt("room_id")));
                        }
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }

            synchronized (this.filterSettings)
            {
                try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM navigator_filter"))
                {
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
                            }
                            catch (Exception e)
                            {
                                continue;
                            }
                        }

                        if (field != null)
                        {
                            this.filterSettings.put(set.getString("key"), new NavigatorFilterField(set.getString("key"), field, set.getString("database_query"), NavigatorFilterComparator.valueOf(set.getString("compare").toUpperCase())));
                        }
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public NavigatorFilterComparator comperatorForField(Method field)
    {
        for (Map.Entry<String, NavigatorFilterField> set : this.filterSettings.entrySet())
        {
            if (set.getValue().field == field)
            {
                return set.getValue().comparator;
            }
        }

        return null;
    }
}
