package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.outgoing.modtool.*;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import io.netty.channel.Channel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class ModToolManager
{
    private final TIntObjectMap<ModToolCategory> category;
    private final THashMap<String, THashSet<String>> presets;
    private final THashMap<Integer, ModToolIssue> tickets;

    public ModToolManager()
    {
        long millis = System.currentTimeMillis();
        this.category = TCollections.synchronizedMap(new TIntObjectHashMap<ModToolCategory>());
        this.presets = new THashMap<String, THashSet<String>>();
        this.tickets = new THashMap<Integer, ModToolIssue>();
        this.loadModTool();
        Emulator.getLogging().logStart("ModTool Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void loadModTool()
    {
        synchronized (this)
        {
            this.category.clear();
            this.presets.clear();
            this.presets.put("user", new THashSet<String>());
            this.presets.put("room", new THashSet<String>());
            this.loadCategory();
            this.loadPresets();
            this.loadTickets();
        }
    }

    private void loadCategory()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM support_issue_categories");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                this.category.put(set.getInt("id"), new ModToolCategory(set.getString("name")));
                PreparedStatement settings = Emulator.getDatabase().prepare("SELECT * FROM support_issue_presets WHERE category = ?");
                settings.setInt(1, set.getInt("id"));
                ResultSet presets = settings.executeQuery();

                while(presets.next())
                {
                    this.category.get(set.getInt("id")).addPreset(new ModToolPreset(presets));
                }
                presets.close();
                settings.close();
                settings.getConnection().close();
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

    private void loadPresets()
    {
        synchronized (this.presets)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM support_presets");
                ResultSet set = statement.executeQuery();
                while (set.next())
                {
                    this.presets.get(set.getString("type")).add(set.getString("preset"));
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

    private void loadTickets()
    {
        synchronized (this.tickets)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT S.username as sender_username, R.username AS reported_username, M.username as mod_username, support_tickets.* FROM support_tickets INNER JOIN users as S ON S.id = sender_id INNER JOIN users AS R ON R.id = reported_id INNER JOIN users AS M ON M.id = mod_id WHERE state != 0");
                ResultSet set = statement.executeQuery();

                while(set.next())
                {
                    this.tickets.put(set.getInt("id"), new ModToolIssue(set));
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

    public void quickTicket(Habbo reported, String reason, String message)
    {
        ModToolIssue issue = new ModToolIssue(0, reason, reported.getHabboInfo().getId(), reported.getHabboInfo().getUsername(), 0, message, ModToolTicketType.AUTOMATIC);
        Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
        Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);
        Emulator.getLogging().logUserLine(message);
    }

    public static void requestUserInfo(GameClient client, ClientMessage packet)
    {
        int userId = packet.readInt();

        if(userId <= 0)
            return;

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE users.id = ? LIMIT 1");
            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();
            while(set.next())
            {
                client.sendResponse(new ModToolUserInfoComposer(set));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<ModToolChatLog> getRoomChatlog(int roomId)
    {
        ArrayList<ModToolChatLog> chatlogs = new ArrayList<ModToolChatLog>();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT users.username, users.id, chatlogs_room.* FROM chatlogs_room INNER JOIN users ON users.id = chatlogs_room.user_from_id WHERE room_id = ? ORDER BY timestamp DESC LIMIT 150");
            statement.setInt(1, roomId);
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                chatlogs.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("id"), set.getString("username"), set.getString("message")));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return chatlogs;
    }

    public ArrayList<ModToolChatLog> getUserChatlog(int userId)
    {
        ArrayList<ModToolChatLog> chatlogs = new ArrayList<ModToolChatLog>();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT users.username, users.id, chatlogs_room.* FROM chatlogs_room INNER JOIN users ON users.id = chatlogs_room.user_from_id WHERE user_from_id = ? ORDER BY timestamp DESC LIMIT 150");
            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                chatlogs.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("id"), set.getString("username"), set.getString("message")));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return chatlogs;
    }

    public ArrayList<ModToolRoomVisit> getUserRoomVisitsAndChatlogs(int userId)
    {
        ArrayList<ModToolRoomVisit> chatlogs = new ArrayList<ModToolRoomVisit>();
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT rooms.name, users.username, room_enter_log.timestamp AS enter_timestamp, room_enter_log.exit_timestamp, chatlogs_room.* FROM room_enter_log INNER JOIN rooms ON room_enter_log.room_id = rooms.id INNER JOIN users ON room_enter_log.user_id = users.id LEFT JOIN chatlogs_room ON room_enter_log.user_id = chatlogs_room.user_from_id AND room_enter_log.room_id = chatlogs_room.room_id AND chatlogs_room.timestamp >= room_enter_log.timestamp AND chatlogs_room.timestamp < room_enter_log.exit_timestamp WHERE chatlogs_room.user_from_id = ? ORDER BY room_enter_log.timestamp DESC LIMIT 500");

            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();

            int userid = 0;
            String username = "unknown";

            while(set.next())
            {
                ModToolRoomVisit visit = null;

                for(ModToolRoomVisit v : chatlogs)
                {
                    if(v.timestamp == set.getInt("enter_timestamp") && v.exitTimestamp == set.getInt("exit_timestamp"))
                    {
                        visit = v;
                    }
                }

                if(visit == null)
                {
                    visit = new ModToolRoomVisit(set.getInt("room_id"), set.getString("name"), set.getInt("enter_timestamp"), set.getInt("exit_timestamp"));
                    chatlogs.add(visit);
                }
                visit.chat.add(new ModToolChatLog(set.getInt("timestamp"), set.getInt("user_from_id"), set.getString("username"), set.getString("message")));

                if(userid == 0)
                    userid = set.getInt("user_from_id");

                if(username.equals("unknown"))
                    username = set.getString("username");
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return chatlogs;
    }

    public THashSet<ModToolRoomVisit> requestUserRoomVisits(Habbo habbo)
    {
        THashSet<ModToolRoomVisit> roomVisits = new THashSet<ModToolRoomVisit>();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT rooms.name, room_enter_log.* FROM room_enter_log INNER JOIN rooms ON rooms.id = room_enter_log.room_id WHERE user_id = ? AND timestamp >= ? ORDER BY timestamp DESC LIMIT 50");
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, Emulator.getIntUnixTimestamp() - 84600);
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                roomVisits.add(new ModToolRoomVisit(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return roomVisits;
    }

    public THashSet<ModToolRoomVisit> getVisitsForRoom(Room room, int amount, boolean groupUser, int fromTimestamp, int toTimestamp)
    {
        return this.getVisitsForRoom(room, amount, groupUser, fromTimestamp, toTimestamp, "");
    }

    public THashSet<ModToolRoomVisit> getVisitsForRoom(Room room, int amount, boolean groupUser, int fromTimestamp, int toTimestamp, String excludeUsername)
    {
        THashSet<ModToolRoomVisit> roomVisits = new THashSet<ModToolRoomVisit>();

        PreparedStatement statement = null;
        try
        {
            String query = "SELECT * FROM (" +
                    "SELECT " +
                    "users.username as name, " +
                    "room_enter_log.* " +
                    "FROM" +
                    "`room_enter_log` " +
                    "INNER JOIN " +
                    "users " +
                    "ON " +
                    "users.id = room_enter_log.user_id " +
                    "WHERE " +
                    "room_enter_log.room_id = ? " +
                    (fromTimestamp > 0 ? "AND timestamp >= ? " : "") +
                    (toTimestamp > 0 ? "AND exit_timestamp <= ? " : "") +
                    "AND users.username != ? " +
                    "ORDER BY " +
                    "timestamp " +
                    "DESC LIMIT ?) x " +
                    (groupUser ? "GROUP BY user_id" : "") +
                    ";";
            statement = Emulator.getDatabase().prepare(query);
            statement.setInt(1, room.getId());

            if(fromTimestamp > 0)
                statement.setInt(2, fromTimestamp);

            if(toTimestamp > 0)
                statement.setInt((fromTimestamp > 0 ? 3 : 2), toTimestamp);

            statement.setString((toTimestamp > 0 ? fromTimestamp > 0 ? 4 : 3 : 2), excludeUsername);

            int columnAmount = 3;
            if(fromTimestamp > 0)
                columnAmount++;

            if(toTimestamp > 0)
                columnAmount++;

            statement.setInt(columnAmount, amount);

            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                roomVisits.add(new ModToolRoomVisit(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
            try
            {
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException ex)
            {
                Emulator.getLogging().logSQLException(ex);
            }
        }

        return roomVisits;
    }

    public ModToolBan createBan(Habbo target, Habbo staff, int expireDate, String reason, String type)
    {
        return createBan(target.getHabboInfo().getId(), ModToolManager.cleanIp(target.getClient().getChannel()), staff, expireDate, reason, type);
    }

    public ModToolBan createBan(int target, String ip, Habbo staff, int expireDate, String reason, String type)
    {
        if(staff.hasPermission("acc_supporttool"))
        {
            ModToolBan ban = new ModToolBan(target, ip, staff.getHabboInfo().getId(), expireDate, reason, type);
            Emulator.getThreading().run(ban);

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(target);

            if(habbo != null)
                Emulator.getGameServer().getGameClientManager().disposeClient(habbo.getClient().getChannel());

            return ban;
        }

        return null;
    }

    public ModToolBan checkForBan(int userId)
    {
        ModToolBan ban = null;
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM bans WHERE user_id = ? AND ban_expire >= ? AND type = 'account' LIMIT 1");
            statement.setInt(1, userId);
            statement.setInt(2, Emulator.getIntUnixTimestamp());
            ResultSet set = statement.executeQuery();

            if(set.next())
            {
                ban = new ModToolBan(set);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return ban;
    }

    public boolean hasIPBan(Channel habbo)
    {
        boolean banned = false;
        PreparedStatement statement = null;
        try
        {
            statement = Emulator.getDatabase().prepare("SELECT * FROM bans WHERE ip = ? AND type = 'ip' AND ban_expire > ? LIMIT 1");
            statement.setString(1, ModToolManager.cleanIp(habbo));
            statement.setInt(2, Emulator.getIntUnixTimestamp());
            ResultSet set = statement.executeQuery();

            if (set.next())
            {
                banned = true;
            }

            set.close();
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

        return banned;
    }

    public boolean unban(String username)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE bans INNER JOIN users ON bans.user_id = users.id SET ban_expire = ?, ban_reason = CONCAT('" + Emulator.getTexts().getValue("unbanned") + ": ', ban_reason) WHERE users.username = ? AND ban_expire > ?");
            statement.setInt(1, Emulator.getIntUnixTimestamp());
            statement.setString(2, username);
            statement.setInt(3, Emulator.getIntUnixTimestamp());
            statement.execute();

            int count = statement.getUpdateCount();

            statement.close();
            statement.getConnection().close();

            return count > 0;
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }

    public void pickTicket(ModToolIssue issue, Habbo habbo)
    {
        issue.modId   = habbo.getHabboInfo().getId();
        issue.modName = habbo.getHabboInfo().getUsername();
        issue.state   = ModToolTicketState.PICKED;

        this.updateTicketToMods(issue);
        issue.updateInDatabase();
    }

    public void updateTicketToMods(ModToolIssue issue)
    {
        Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(new ModToolIssueInfoComposer(issue).compose(), "acc_supporttool");
    }

    public void addTicket(ModToolIssue issue)
    {
        synchronized (this.tickets)
        {
            this.tickets.put(issue.id, issue);
        }
    }

    public void removeTicket(ModToolIssue issue)
    {
        this.removeTicket(issue.id);
    }

    public void removeTicket(int issueId)
    {
        synchronized (this.tickets)
        {
            this.tickets.remove(issueId);
        }
    }

    public void closeTicketAsUseless(ModToolIssue issue, Habbo sender)
    {
        issue.state = ModToolTicketState.CLOSED;
        issue.updateInDatabase();

        if(sender != null)
        {
            sender.getClient().sendResponse(new ModToolIssueHandledComposer(ModToolIssueHandledComposer.USELESS));
        }

        this.updateTicketToMods(issue);

        this.removeTicket(issue);
    }

    public void closeTicketAsAbusive(ModToolIssue issue, Habbo sender)
    {
        issue.state = ModToolTicketState.CLOSED;
        issue.updateInDatabase();
        if(sender != null)
        {
            sender.getClient().sendResponse(new ModToolIssueHandledComposer(ModToolIssueHandledComposer.ABUSIVE));
        }

        this.updateTicketToMods(issue);

        this.removeTicket(issue);
    }

    public void closeTicketAsHandled(ModToolIssue issue, Habbo sender)
    {
        issue.state = ModToolTicketState.CLOSED;
        issue.updateInDatabase();

        if(sender != null)
        {
            sender.getClient().sendResponse(new ModToolIssueHandledComposer(ModToolIssueHandledComposer.HANDLED));
        }

        this.updateTicketToMods(issue);

        this.removeTicket(issue);
    }

    public boolean hasPendingTickets(int userId)
    {
        synchronized (this.tickets)
        {
            for(Map.Entry<Integer, ModToolIssue> map : this.tickets.entrySet())
            {
                if(map.getValue().senderId == userId)
                    return true;
            }
        }

        return false;
    }

    public TIntObjectMap<ModToolCategory> getCategory()
    {
        return this.category;
    }

    public THashMap<String, THashSet<String>> getPresets()
    {
        return this.presets;
    }

    public THashMap<Integer, ModToolIssue> getTickets()
    {
        return this.tickets;
    }

    public ModToolIssue getTicket(int ticketId)
    {
        return this.tickets.get(ticketId);
    }

    public static String cleanIp(Channel channel)
    {
        try
        {
            return channel.remoteAddress().toString().split(":")[0].replace("/", "");
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
