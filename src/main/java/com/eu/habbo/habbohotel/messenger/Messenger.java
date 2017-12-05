package com.eu.habbo.habbohotel.messenger;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.friends.UpdateFriendComposer;
import com.eu.habbo.plugin.events.users.friends.UserAcceptFriendRequestEvent;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Messenger
{
    //Configuration. Loaded from database & updated accordingly.
    public static boolean SAVE_PRIVATE_CHATS = false;
    public static int MAXIMUM_FRIENDS = 200;

    private final ConcurrentHashMap<Integer, MessengerBuddy> friends;
    private final THashSet<FriendRequest> friendRequests;

    public Messenger()
    {
        this.friends = new ConcurrentHashMap<Integer, MessengerBuddy>();
        this.friendRequests = new THashSet<FriendRequest>();
    }

    public void loadFriends(Habbo habbo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT " +
                    "users.id, " +
                    "users.username, " +
                    "users.gender, " +
                    "users.online, " +
                    "users.look, " +
                    "users.motto, " +
                    "messenger_friendships.* FROM messenger_friendships INNER JOIN users ON messenger_friendships.user_two_id = users.id WHERE user_one_id = ?"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    this.friends.putIfAbsent(set.getInt("id"), new MessengerBuddy(set));
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public MessengerBuddy loadFriend(Habbo habbo, int userId)
    {
        MessengerBuddy buddy = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT " +
                    "users.id, " +
                    "users.username, " +
                    "users.gender, " +
                    "users.online, " +
                    "users.look, " +
                    "users.motto, " +
                    "messenger_friendships.* FROM messenger_friendships INNER JOIN users ON messenger_friendships.user_two_id = users.id WHERE user_one_id = ? AND user_two_id = ? LIMIT 1"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, userId);

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    buddy = new MessengerBuddy(set);
                    this.friends.putIfAbsent(set.getInt("id"), buddy);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return buddy;
    }

    public void loadFriendRequests(Habbo habbo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.id, users.username, users.look FROM messenger_friendrequests INNER JOIN users ON user_from_id = users.id WHERE user_to_id = ?"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                while(set.next())
                {
                    friendRequests.add(new FriendRequest(set));
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void removeBuddy(int id)
    {
        this.friends.remove(id);
    }

    public void removeBuddy(Habbo habbo)
    {
        this.friends.remove(habbo.getHabboInfo().getId());
    }

    public static void unfriend(int userOne, int userTwo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM messenger_friendships WHERE (user_one_id = ? AND user_two_id = ?) OR (user_one_id = ? AND user_two_id = ?)"))
        {
            statement.setInt(1, userOne);
            statement.setInt(2, userTwo);
            statement.setInt(3, userTwo);
            statement.setInt(4, userOne);
            statement.execute();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public THashSet<MessengerBuddy> getFriends(String username)
    {
        THashSet<MessengerBuddy> users = new THashSet<MessengerBuddy>();

        for(Map.Entry<Integer, MessengerBuddy> map : this.friends.entrySet())
        {
            if(map.getValue().getUsername().contains(username))
            {
                users.add(map.getValue());
            }
        }

        return users;
    }
    public static THashSet<MessengerBuddy> searchUsers(String username)
    {
        THashSet<MessengerBuddy> users = new THashSet<MessengerBuddy>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username LIKE ? ORDER BY username DESC LIMIT 50"))
        {
            statement.setString(1, username + "%");
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    users.add(new MessengerBuddy(set, false));
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        return users;
    }

    public void connectionChanged(Habbo owner, boolean online, boolean inRoom)
    {
        if(owner != null)
        {
            for (Map.Entry<Integer, MessengerBuddy> map : this.getFriends().entrySet())
            {
                if (map.getValue().getOnline() == 0)
                    continue;

                Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(map.getKey());

                if (habbo != null)
                {
                    if (habbo.getMessenger() != null)
                    {
                        MessengerBuddy buddy = habbo.getMessenger().getFriend(owner.getHabboInfo().getId());

                        if (buddy != null)
                        {
                            buddy.setOnline(online);
                            buddy.inRoom(inRoom);
                            buddy.setLook(owner.getHabboInfo().getLook());
                            buddy.setGender(owner.getHabboInfo().getGender());
                            buddy.setUsername(owner.getHabboInfo().getUsername());
                            Emulator.getGameServer().getGameClientManager().getClient(habbo).sendResponse(new UpdateFriendComposer(buddy));
                        }
                    }
                }
            }
        }
    }

    public void deleteAllFriendRequests(int userTo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM messenger_friendrequests WHERE user_to_id = ?"))
        {
            statement.setInt(1, userTo);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public int deleteFriendRequests(int userFrom, int userTo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM messenger_friendrequests WHERE (user_to_id = ? AND user_from_id = ?) OR (user_to_id = ? AND user_from_id = ?)"))
        {
            statement.setInt(1, userTo);
            statement.setInt(2, userFrom);
            statement.setInt(4, userTo);
            statement.setInt(3, userFrom);
            return statement.executeUpdate();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return 0;
    }
    //TODO Needs redesign. userFrom is redundant.
    public void acceptFriendRequest(int userFrom, int userTo)
    {
        int count = deleteFriendRequests(userFrom, userTo);

        if(count > 0)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO messenger_friendships (user_one_id, user_two_id, friends_since) VALUES (?, ?, ?)"))
            {
                statement.setInt(1, userFrom);
                statement.setInt(2, userTo);
                statement.setInt(3, Emulator.getIntUnixTimestamp());
                statement.execute();

                statement.setInt(1, userTo);
                statement.setInt(2, userFrom);
                statement.setInt(3, Emulator.getIntUnixTimestamp());
                statement.execute();
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            Habbo habboTo = Emulator.getGameServer().getGameClientManager().getHabbo(userTo);
            Habbo habboFrom = Emulator.getGameServer().getGameClientManager().getHabbo(userFrom);

            if(habboTo != null && habboFrom != null)
            {
                MessengerBuddy to = new MessengerBuddy(habboFrom, habboTo.getHabboInfo().getId());
                MessengerBuddy from = new MessengerBuddy(habboTo, habboFrom.getHabboInfo().getId());


                habboTo.getMessenger().friends.putIfAbsent(habboFrom.getHabboInfo().getId(), to);
                habboFrom.getMessenger().friends.putIfAbsent(habboTo.getHabboInfo().getId(), from);

                if(Emulator.getPluginManager().fireEvent(new UserAcceptFriendRequestEvent(habboTo, from)).isCancelled())
                {
                    this.removeBuddy(userTo);
                    return;
                }

                Emulator.getGameServer().getGameClientManager().getClient(habboTo).sendResponse(new UpdateFriendComposer(to));
                Emulator.getGameServer().getGameClientManager().getClient(habboFrom).sendResponse(new UpdateFriendComposer(from));
            }
            else if (habboTo != null)
            {
                habboTo.getClient().sendResponse(new UpdateFriendComposer(loadFriend(habboTo, userFrom)));
            }
            else if (habboFrom != null)
            {
                habboFrom.getClient().sendResponse(new UpdateFriendComposer(loadFriend(habboFrom, userTo)));
            }
        }
    }

    public static boolean canFriendRequest(Habbo habbo, String friend)
    {
        Habbo user = Emulator.getGameEnvironment().getHabboManager().getHabbo(friend);
        HabboInfo habboInfo;

        if(user != null)
        {
            habboInfo = user.getHabboInfo();
        }
        else
        {
            habboInfo = HabboManager.getOfflineHabboInfo(friend);
        }

        if(habboInfo == null)
        {
            return false;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM messenger_friendrequests WHERE (user_to_id = ? AND user_from_id = ?) OR (user_to_id = ? AND user_from_id = ?) LIMIT 1"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, habboInfo.getId());
            statement.setInt(3, habboInfo.getId());
            statement.setInt(4, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                if (!set.next())
                {
                    return true;
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }

    public static boolean friendRequested(int userFrom, int userTo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM messenger_friendrequests WHERE user_to_id = ? AND user_from_id = ? LIMIT 1"))
        {
            statement.setInt(1, userFrom);
            statement.setInt(2, userTo);

            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    return true;
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }

    public static void makeFriendRequest(int userFrom, int userTo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO messenger_friendrequests (user_to_id, user_from_id) VALUES (?, ?)"))
        {
            statement.setInt(1, userTo);
            statement.setInt(2, userFrom);
            statement.executeUpdate();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public static int getFriendCount(int userId)
    {
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(userId);

        if(habbo != null)
            return habbo.getMessenger().getFriends().size();

        int count = 0;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT count(id) as count FROM messenger_friendships WHERE user_one_id = ?"))
        {
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                    count = set.getInt("count");
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return count;
    }

    public static THashMap<Integer, THashSet<MessengerBuddy>> getFriends(int userId)
    {
        THashMap<Integer, THashSet<MessengerBuddy>> map = new THashMap<Integer, THashSet<MessengerBuddy>>();
        map.put(1, new THashSet<MessengerBuddy>());
        map.put(2, new THashSet<MessengerBuddy>());
        map.put(3, new THashSet<MessengerBuddy>());

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.id, users.look, users.username, messenger_friendships.relation FROM messenger_friendships INNER JOIN users ON users.id = messenger_friendships.user_two_id WHERE user_one_id = ? ORDER BY RAND() LIMIT 50"))
        {
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (set.getInt("relation") == 0)
                        continue;

                    MessengerBuddy buddy = new MessengerBuddy(set.getInt("id"), set.getString("username"), set.getString("look"), (short) set.getInt("relation"), userId);
                    map.get((int) buddy.getRelation()).add(buddy);
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        return map;
    }

    public ConcurrentHashMap<Integer, MessengerBuddy> getFriends()
    {
        return this.friends;
    }

    public THashSet<FriendRequest> getFriendRequests()
    {
        synchronized (this.friendRequests)
        {
            return this.friendRequests;
        }
    }

    public FriendRequest findFriendRequest(String username)
    {
        synchronized (this.friendRequests)
        {
            for (FriendRequest friendRequest : this.friendRequests)
            {
                if (friendRequest.getUsername().equalsIgnoreCase(username))
                {
                    return friendRequest;
                }
            }
        }

        return null;
    }

    public MessengerBuddy getFriend(int id)
    {
        return this.friends.get(id);
    }

    public void dispose()
    {
		synchronized (this.friends)
		{
			this.friends.clear();
		}
        
		synchronized (this.friendRequests)
		{
			this.friendRequests.clear();
		}
	}

}
