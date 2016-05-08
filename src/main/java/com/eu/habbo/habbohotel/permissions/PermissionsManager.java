package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.HabboPlugin;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created on 11-10-2014 14:10.
 */
public class PermissionsManager
{
    private final THashMap<Integer, THashSet<String>> permissions;
    private final THashMap<Integer, String> rankNames;
    private final TIntIntHashMap enables;

    public PermissionsManager()
    {
        long millis      = System.currentTimeMillis();
        this.permissions = new THashMap<Integer, THashSet<String>>();
        this.rankNames   = new THashMap<Integer, String>();
        this.enables     = new TIntIntHashMap();

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

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM permissions ORDER BY id ASC");
                ResultSet set = statement.executeQuery();
                ResultSetMetaData meta = set.getMetaData();

                while (set.next())
                {
                    THashSet<String> names = new THashSet<String>();

                    for (int i = 1; i < meta.getColumnCount() + 1; i++)
                    {
                        if (meta.getColumnName(i).startsWith("cmd_") || meta.getColumnName(i).startsWith("acc_"))
                        {
                            if (set.getString(i).equals("1"))
                            {
                                names.add(meta.getColumnName(i));
                            }
                        }
                        else if(meta.getColumnName(i).equalsIgnoreCase("rank_name"))
                        {
                            rankNames.put(set.getInt("id"), set.getString(i));
                        }
                    }

                    permissions.put(set.getInt("id"), names);
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
    }

    private void loadEnables()
    {
        synchronized (this.enables)
        {
            this.enables.clear();

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM special_enables");
                ResultSet set = statement.executeQuery();

                while(set.next())
                {
                    this.enables.put(set.getInt("effect_id"), set.getInt("min_rank"));
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
    }

    public THashSet<String> getPermissionsForRank(int rankId)
    {
        if(this.permissions.containsKey(rankId))
        {
            return this.permissions.get(rankId);
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

    public boolean isEffectBlocked(int effectId, int rank)
    {
        return this.enables.contains(effectId) && this.enables.get(effectId) > rank;
    }

    public boolean hasPermission(Habbo habbo, String permission)
    {
        boolean result = this.hasPermission(habbo.getHabboInfo().getRank(), permission);

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

        return true;
    }

    public boolean hasPermission(int rankId, String permission)
    {
        return this.permissions.containsKey(rankId) && this.permissions.get(rankId).contains(permission);
    }
}
