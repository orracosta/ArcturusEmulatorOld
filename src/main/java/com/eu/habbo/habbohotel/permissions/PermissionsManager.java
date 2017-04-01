package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.HabboPlugin;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PermissionsManager
{
    private final THashMap<Integer, THashMap<String, Integer>> permissions;
    private final THashMap<Integer, String> rankNames;
    private final TIntIntHashMap enables;
    private final TIntIntHashMap roomEffect;

    public PermissionsManager()
    {
        long millis      = System.currentTimeMillis();
        this.permissions = new THashMap<Integer, THashMap<String, Integer>>();
        this.rankNames   = new THashMap<Integer, String>();
        this.enables     = new TIntIntHashMap();
        this.roomEffect  = new TIntIntHashMap();

        this.reload();

        Emulator.getLogging().logStart("Permissions Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reload()
    {
        this.loadPermissions();
        this.loadEnables();
    }

    private void loadPermissions()
    {
        synchronized (this.permissions)
        {
            this.permissions.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permissions ORDER BY id ASC"))
            {
                try (ResultSet set = statement.executeQuery())
                {
                    ResultSetMetaData meta = set.getMetaData();

                    while (set.next())
                    {
                        THashMap<String, Integer> names = new THashMap<String, Integer>();

                        for (int i = 1; i < meta.getColumnCount() + 1; i++)
                        {
                            if (meta.getColumnName(i).startsWith("cmd_") || meta.getColumnName(i).startsWith("acc_"))
                            {
                                if (set.getString(i).equals("1") || set.getString(i).equals("2"))
                                {
                                    names.put(meta.getColumnName(i), Integer.valueOf(set.getString(i)));
                                }
                            }
                            else if (meta.getColumnName(i).equalsIgnoreCase("rank_name"))
                            {
                                this.rankNames.put(set.getInt("id"), set.getString(i));
                            }
                            else if (meta.getColumnName(i).equalsIgnoreCase("room_effect"))
                            {
                                this.roomEffect.put(set.getInt("id"), set.getInt(i));
                            }
                            else if (meta.getCatalogName(i).equalsIgnoreCase("log_commands"))
                            {
                                names.put(meta.getCatalogName(i), Integer.valueOf(set.getString(i)));
                            }
                        }

                        this.permissions.put(set.getInt("id"), names);
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    private void loadEnables()
    {
        synchronized (this.enables)
        {
            this.enables.clear();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM special_enables"))
            {
                while(set.next())
                {
                    this.enables.put(set.getInt("effect_id"), set.getInt("min_rank"));
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public Collection<String> getPermissionsForRank(int rankId)
    {
        if(this.permissions.containsKey(rankId))
        {
            return this.permissions.get(rankId).keySet();
        }

        return new THashSet<String>();
    }

    public String getRankName(int rankId)
    {
        return this.rankNames.get(rankId);
    }

    public int getRankId(String name)
    {
        for(Map.Entry<Integer, String> map : this.rankNames.entrySet())
        {
            if(map.getValue().equalsIgnoreCase(name))
                return map.getKey();
        }

        return -1;
    }

    public int getEffect(int rankId)
    {
        return this.roomEffect.get(rankId);
    }

    public boolean isEffectBlocked(int effectId, int rank)
    {
        return this.enables.contains(effectId) && this.enables.get(effectId) > rank;
    }

    public boolean hasPermission(Habbo habbo, String permission)
    {
        return this.hasPermission(habbo, permission, false);
    }

    public boolean hasPermission(Habbo habbo, String permission, boolean withRoomRights)
    {
        boolean result = this.hasPermission(habbo.getHabboInfo().getRank(), permission, withRoomRights);

        if (!result)
        {
            for (HabboPlugin plugin : Emulator.getPluginManager().getPlugins())
            {
                if(plugin.hasPermission(habbo, permission))
                {
                    return true;
                }
            }

            return false;
        }

        return result;
    }

    public boolean hasPermission(int rankId, String permission, boolean withRoomRights)
    {
        if (this.permissions.containsKey(rankId))
        {
            if (this.permissions.get(rankId).containsKey(permission))
            {
                int rightsLevel = this.permissions.get(rankId).get(permission);

                if (rightsLevel == 1)
                    return true;

                else if (rightsLevel == 2 && withRoomRights)
                    return true;
            }
        }

        return false;
    }
}
