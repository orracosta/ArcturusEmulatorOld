package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import gnu.trove.set.hash.THashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BadgesComponent
{
    private final THashSet<HabboBadge> badges = new THashSet<HabboBadge>();

    public BadgesComponent(Habbo habbo)
    {
        this.badges.addAll(loadBadges(habbo));
    }

    private static THashSet<HabboBadge> loadBadges(Habbo habbo)
    {
        THashSet<HabboBadge> badgesList = new THashSet<HabboBadge>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_badges WHERE user_id = ?"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    badgesList.add(new HabboBadge(set, habbo));
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return badgesList;
    }

    public static void resetSlots(Habbo habbo)
    {
        for(HabboBadge badge : habbo.getInventory().getBadgesComponent().getBadges())
        {
            if(badge.getSlot() == 0)
                continue;

            badge.setSlot(0);
            badge.needsUpdate(true);
            Emulator.getThreading().run(badge);
        }
    }

    public ArrayList<HabboBadge> getWearingBadges()
    {
        synchronized (this.badges)
        {
            ArrayList<HabboBadge> badgesList = new ArrayList<HabboBadge>();
            for (HabboBadge badge : this.badges)
            {
                if (badge.getSlot() == 0)
                    continue;

                badgesList.add(badge);
            }

            Collections.sort(badgesList, new Comparator<HabboBadge>()
            {
                @Override
                public int compare(HabboBadge o1, HabboBadge o2)
                {
                    return o1.getSlot() - o2.getSlot();
                }
            });
            return badgesList;
        }
    }

    public THashSet<HabboBadge> getBadges()
    {
        return this.badges;
    }

    public static ArrayList<HabboBadge> getBadgesOfflineHabbo(int userId)
    {
        ArrayList<HabboBadge> badgesList = new ArrayList<HabboBadge>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_badges WHERE slot_id > 0 AND user_id = ? ORDER BY slot_id ASC"))
        {
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    badgesList.add(new HabboBadge(set, null));
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        return badgesList;
    }

    public boolean hasBadge(String badge)
    {
        return this.getBadge(badge) != null;
    }

    public HabboBadge getBadge(String badgeCode)
    {
        synchronized (this.badges)
        {
            for (HabboBadge badge : this.badges)
            {
                if (badge.getCode().equalsIgnoreCase(badgeCode))
                    return badge;
            }
            return null;
        }
    }

    public void addBadge(HabboBadge badge)
    {
        synchronized (this.badges)
        {
            this.badges.add(badge);
        }
    }

    public HabboBadge removeBadge(String badge)
    {
        synchronized (this.badges)
        {
            for(HabboBadge b : this.badges)
            {
                if(b.getCode().equalsIgnoreCase(badge))
                {
                    this.badges.remove(b);
                    return b;
                }
            }
        }

        return null;
    }

    public void removeBadge(HabboBadge badge)
    {
        synchronized (this.badges)
        {
            this.badges.remove(badge);
        }
    }

    public void dispose()
    {
        synchronized (this.badges)
        {
            this.badges.clear();
        }
    }

    public static HabboBadge createBadge(String code, Habbo habbo)
    {
        HabboBadge badge = new HabboBadge(0, code, 0, habbo);
        badge.run();
        habbo.getInventory().getBadgesComponent().addBadge(badge);
        return badge;
    }


    public static void deleteBadge(String username, HabboBadge badge)
    {
        deleteBadge(username, badge.getCode());
    }

    public static void deleteBadge(String username, String badge)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE users_badges FROM users_badges INNER JOIN users ON users_badges.user_id = users.id WHERE users.username LIKE ? AND badge_code LIKE ?"))
        {
            statement.setString(1, username);
            statement.setString(2, badge);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
