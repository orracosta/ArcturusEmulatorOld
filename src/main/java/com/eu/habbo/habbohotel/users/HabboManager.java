package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.users.UserPermissionsComposer;
import com.eu.habbo.messages.rcon.RCONMessage;
import com.eu.habbo.plugin.events.users.UserRegisteredEvent;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HabboManager
{
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
        PreparedStatement statement = null;
        try
        {
            statement = Emulator.getDatabase().prepare("SELECT * FROM users WHERE auth_ticket = ? LIMIT 1");
            statement.setString(1, sso);
            ResultSet set = statement.executeQuery();
            if(set.next())
            {
                habbo = cloneCheck(set.getInt("id"));
                if(habbo != null)
                {
                    habbo.getClient().sendResponse(new GenericAlertComposer("You logged in from somewhere else."));
                    habbo.getClient().getChannel().close();
                    habbo = null;
                }

                ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().checkForBan(set.getInt("id"));
                if(ban != null)
                {
                    set.close();
                    statement.close();
                    statement.getConnection().close();
                    return null;
                }

                habbo = new Habbo(set);

                if (habbo.firstVisit)
                {
                    Emulator.getPluginManager().fireEvent(new UserRegisteredEvent(habbo));
                }

                if (!Emulator.debugging)
                {
                    PreparedStatement ssoStatement = null;

                    try
                    {
                        ssoStatement = Emulator.getDatabase().prepare("UPDATE users SET auth_ticket = ? WHERE auth_ticket = ? AND users.id = ? LIMIT 1");
                        ssoStatement.setString(1, new String(""));
                        ssoStatement.setString(2, sso);
                        ssoStatement.setInt(3, habbo.getHabboInfo().getId());
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                    finally
                    {
                        if (ssoStatement != null)
                        {
                            try
                            {
                                ssoStatement.close();
                                ssoStatement.getConnection().close();
                            }
                            catch (SQLException e)
                            {
                                Emulator.getLogging().logSQLException(e);
                            }
                        }
                    }
                }
            }

            set.close();

        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
        finally
        {
            if (statement != null)
            {
                try
                {
                    statement.close();
                    statement.getConnection().close();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }

        return habbo;
    }

    public static HabboInfo getOfflineHabboInfo(int id)
    {
        HabboInfo info = null;
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users WHERE id = ? LIMIT 1");
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();

            if(set.next())
            {
                info = new HabboInfo(set);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
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

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users WHERE username = ? LIMIT 1");
            statement.setString(1, username);

            ResultSet set = statement.executeQuery();

            if(set.next())
            {
                info = new HabboInfo(set);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
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

    public void dispose()
    {
        THashSet<Habbo> toDisconnect = new THashSet<Habbo>();
        toDisconnect.addAll(this.onlineHabbos.values());

        synchronized (this.onlineHabbos)
        {
            for (final Habbo habbo : toDisconnect)
            {
                habbo.disconnect();
            }
        }
        toDisconnect.clear();
        this.onlineHabbos.clear();

        Emulator.getLogging().logShutdownLine("Habbo Manager -> Disposed!");
    }

    public ArrayList<HabboInfo> getCloneAccounts(Habbo habbo, int limit)
    {
        ArrayList<HabboInfo> habboInfo = new ArrayList<HabboInfo>();
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users WHERE ip_register = ? OR ip_current = ? AND id != ? ORDER BY id DESC LIMIT ?");
            statement.setString(1, habbo.getHabboInfo().getIpRegister());
            statement.setString(2, habbo.getClient().getChannel().remoteAddress().toString());
            statement.setInt(3, habbo.getHabboInfo().getId());
            statement.setInt(4, limit);
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                habboInfo.add(new HabboInfo(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return habboInfo;
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

        if (Emulator.getGameEnvironment().getPermissionsManager().getPermissionsForRank(userId) == null)
        {
            throw new Exception("Rank ID (" + rankId + ") does not exist");
        }

        if(habbo != null)
        {
            habbo.getHabboInfo().setRank(rankId);
            habbo.getClient().sendResponse(new UserPermissionsComposer(habbo));
        }
        else
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users SET rank = ? WHERE id = ? LIMIT 1");

            try
            {
                statement.setInt(1, rankId);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            finally
            {
                if (statement != null)
                {
                    try
                    {
                        statement.close();
                        statement.getConnection().close();
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
            }
        }
    }
}
