package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
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

    public Habbo loadHabbo(String sso, GameClient client)
    {
        Habbo habbo = null;
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users WHERE auth_ticket = ? LIMIT 1");
            statement.setString(1, sso);

            ResultSet set = statement.executeQuery();
            if(set.next())
            {
                Habbo h = cloneCheck(set.getInt("id"));
                if(h != null)
                {
                    //Emulator.getGameServer().getGameClientManager().disposeClient(h.getClient().getChannel());
                    h.getClient().sendResponse(new GenericAlertComposer("You logged in from somewhere else."));
                    h.getClient().getChannel().close();
                    h = null;
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
            }

            set.close();
            statement.close();
            statement.getConnection().close();

        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            for (Habbo habbo : toDisconnect)
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

}
