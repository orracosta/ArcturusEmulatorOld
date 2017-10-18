package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.permissions.Rank;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceConfigComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.eu.habbo.plugin.events.users.UserRegisteredEvent;
import gnu.trove.set.hash.THashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class HabboManager
{
    //Configuration. Loaded from database & updated accordingly.
    public static String WELCOME_MESSAGE = "";
    public static boolean NAMECHANGE_ENABLED = false;

    private final ConcurrentHashMap<Integer, Habbo> onlineHabbos;

    public HabboManager()
    {
        long millis = System.currentTimeMillis();

        this.onlineHabbos = new ConcurrentHashMap<Integer, Habbo>();

        Emulator.getLogging().logStart("Habbo Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void addHabbo(Habbo habbo)
    {
        this.onlineHabbos.put(habbo.getHabboInfo().getId(), habbo);
    }

    public void removeHabbo(Habbo habbo)
    {
        this.onlineHabbos.remove(habbo.getHabboInfo().getId());
    }

    public Habbo getHabbo(int id)
    {
        return this.onlineHabbos.get(id);
    }

    public Habbo getHabbo(String username)
    {
        synchronized (this.onlineHabbos)
        {
            for (Map.Entry<Integer, Habbo> map : this.onlineHabbos.entrySet())
            {
                if (map.getValue().getHabboInfo().getUsername().equalsIgnoreCase(username))
                    return map.getValue();
            }
        }

        return null;
    }

    public Habbo loadHabbo(String sso)
    {
        Habbo habbo = null;
        ResultSet set = null;

        int userId = 0;
        try(Connection connection = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE auth_ticket = ? LIMIT 1"))
        {
            statement.setString(1, sso);
            try (ResultSet s = statement.executeQuery())
            {
                if (s.next())
                {
                    userId = s.getInt("id");
                }
            }
            statement.close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        habbo = cloneCheck(userId);
        if (habbo != null)
        {
            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("loggedin.elsewhere")));
            Emulator.getGameServer().getGameClientManager().disposeClient(habbo.getClient().getChannel());
            habbo = null;
        }

        ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().checkForBan(userId);
        if (ban != null)
        {
            return null;
        }

        try(Connection connection = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE auth_ticket LIKE ? LIMIT 1"))
        {
            statement.setString(1, sso);
            set = statement.executeQuery();

        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception ex)
        {
            Emulator.getLogging().logErrorLine(ex);
        }

        try
        {
            if (set.next())
            {
                habbo = new Habbo(set);

                if (habbo.firstVisit)
                {
                    Emulator.getPluginManager().fireEvent(new UserRegisteredEvent(habbo));
                }

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET auth_ticket = ? WHERE auth_ticket LIKE ? AND id = ? LIMIT 1"))
                {
                    statement.setString(1, null);
                    statement.setString(2, sso);
                    statement.setInt(3, habbo.getHabboInfo().getId());
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }

            set.close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return habbo;
    }

    public static HabboInfo getOfflineHabboInfo(int id)
    {
        HabboInfo info = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, id);
            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    info = new HabboInfo(set);
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return info;
    }

    public static HabboInfo getOfflineHabboInfo(String username)
    {
        HabboInfo info = null;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1"))
        {
            statement.setString(1, username);

            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    info = new HabboInfo(set);
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return info;
    }

    public int getOnlineCount()
    {
        return this.onlineHabbos.size();
    }

    public Habbo cloneCheck(int id)
    {
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(id);

        return habbo;
    }

    public void sendPacketToHabbosWithPermission(ServerMessage message, String perm)
    {
        synchronized (this.onlineHabbos)
        {
            for(Habbo habbo : this.onlineHabbos.values())
            {
                if(habbo.hasPermission(perm))
                {
                    habbo.getClient().sendResponse(message);
                }
            }
        }
    }

    public ConcurrentHashMap<Integer, Habbo> getOnlineHabbos()
    {
        return this.onlineHabbos;
    }

    public synchronized void dispose()
    {
        Object[] toDisconnect = this.onlineHabbos.values().toArray();
        List<ScheduledFuture> scheduledFutures = new ArrayList<>();
        this.onlineHabbos.clear();
        for (Object habbo : toDisconnect)
        {
            scheduledFutures.add(Emulator.getThreading().run(new Runnable()
            {
                @Override
                public void run()
                {
                    ((Habbo) habbo).disconnect();
                }
            }));
        }

        while (!scheduledFutures.isEmpty())
        {
            List<ScheduledFuture> toRemove = new ArrayList<>();
            for (ScheduledFuture future : scheduledFutures)
            {
                if (future.isDone())
                {
                    toRemove.add(future);
                }
            }
            scheduledFutures.removeAll(toRemove);
        }
        Emulator.getLogging().logShutdownLine("Habbo Manager -> Disposed!");
    }

    public ArrayList<HabboInfo> getCloneAccounts(Habbo habbo, int limit)
    {
        ArrayList<HabboInfo> habboInfo = new ArrayList<HabboInfo>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE ip_register = ? OR ip_current = ? AND id != ? ORDER BY id DESC LIMIT ?"))
        {
            statement.setString(1, habbo.getHabboInfo().getIpRegister());
            statement.setString(2, habbo.getClient().getChannel().remoteAddress().toString());
            statement.setInt(3, habbo.getHabboInfo().getId());
            statement.setInt(4, limit);

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    habboInfo.add(new HabboInfo(set));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return habboInfo;
    }

    public List<Map.Entry<Integer, String>> getNameChanges(int userId, int limit)
    {
        List<Map.Entry<Integer, String>> nameChanges = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT timestamp, new_name FROM namechange_log WHERE user_id = ? ORDER by timestamp DESC LIMIT ?"))
        {
            statement.setInt(1, userId);
            statement.setInt(2, limit);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    nameChanges.add(new AbstractMap.SimpleEntry<Integer, String>(set.getInt("timestamp"), set.getString("new_name")));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return nameChanges;
    }

    /**
     * Sets the rank for an user.
     * Updates the database if the user is offline.
     * @param userId The ID of the user.
     * @param rankId The new rank ID for the user.
     * @throws Exception When the rank does not exist.
     */
    public void setRank(int userId, int rankId) throws Exception
    {
        Habbo habbo = this.getHabbo(userId);

        if (!Emulator.getGameEnvironment().getPermissionsManager().rankExists(rankId))
        {
            throw new Exception("Rank ID (" + rankId + ") does not exist");
        }

        Rank rank = Emulator.getGameEnvironment().getPermissionsManager().getRank(rankId);

        if(habbo != null)
        {
            habbo.getHabboInfo().setRank(rank);
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
            habbo.getClient().sendResponse(new UserPerksComposer(habbo));

            if (habbo.hasPermission("acc_supporttool"))
            {
                habbo.getClient().sendResponse(new ModToolComposer(habbo));
            }
            habbo.getHabboInfo().run();

            habbo.getClient().sendResponse(new CatalogUpdatedComposer());
            habbo.getClient().sendResponse(new CatalogModeComposer(0));
            habbo.getClient().sendResponse(new DiscountComposer());
            habbo.getClient().sendResponse(new MarketplaceConfigComposer());
            habbo.getClient().sendResponse(new GiftConfigurationComposer());
            habbo.getClient().sendResponse(new RecyclerLogicComposer());
            habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_give_rank.new_rank").replace("id", rank.getName())));
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET rank = ? WHERE id = ? LIMIT 1"))
            {
                statement.setInt(1, rankId);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public void giveCredits(int userId, int credits)
    {
        Habbo habbo = this.getHabbo(userId);
        if (habbo != null)
        {
            habbo.giveCredits(credits);
        }
        else
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement= connection.prepareStatement("UPDATE users SET credits = credits + ? WHERE id = ? LIMIT 1"))
            {
                statement.setInt(1, credits);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public void staffAlert(String message)
    {
        message = Emulator.getTexts().getValue("commands.generic.cmd_staffalert.title") + "\r\n" + message;
        ServerMessage msg = new GenericAlertComposer(message).compose();
        Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(msg, "cmd_staffalert");
    }
}
