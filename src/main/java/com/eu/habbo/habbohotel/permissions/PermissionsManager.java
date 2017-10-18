package com.eu.habbo.habbohotel.permissions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.HabboPlugin;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PermissionsManager
{
    private final TIntObjectHashMap<Rank> ranks;
    private final TIntIntHashMap enables;

    public PermissionsManager()
    {
        long millis  = System.currentTimeMillis();
        this.ranks   = new TIntObjectHashMap<Rank>();
        this.enables = new TIntIntHashMap();

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
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM permissions ORDER BY id ASC"))
        {
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (!this.ranks.containsKey(set.getInt("id")))
                    {
                        this.ranks.put(set.getInt("id"), new Rank(set));
                    } else
                    {
                        this.ranks.get(set.getInt("id")).load(set);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
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

    public boolean rankExists(int rankId)
    {
        return this.ranks.containsKey(rankId);
    }

    public Rank getRank(int rankId)
    {
        return this.ranks.get(rankId);
    }

    public Rank getRank(String rankName)
    {
        for (Rank rank : this.ranks.valueCollection())
        {
            if (rank.getName().equalsIgnoreCase(rankName))
                return rank;
        }

        return null;
    }

    @Deprecated
    public Collection<String> getPermissionsForRank(int rankId)
    {
        throw new RuntimeException("Please use getRank()");
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

    public boolean hasPermission(Rank rank, String permission, boolean withRoomRights)
    {
        return rank.hasPermission(permission, withRoomRights);
    }
}
