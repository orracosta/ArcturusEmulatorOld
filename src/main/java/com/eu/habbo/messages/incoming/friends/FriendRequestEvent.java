package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.FriendRequest;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendRequestComposer;
import com.eu.habbo.messages.outgoing.friends.FriendRequestErrorComposer;
import com.eu.habbo.plugin.events.users.friends.UserRequestFriendshipEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 25-8-2014 18:11.
 */
public class FriendRequestEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        String username = packet.readString();
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);

        if(Emulator.getPluginManager().fireEvent(new UserRequestFriendshipEvent(this.client.getHabbo(), username, habbo)).isCancelled())
        {
            this.client.sendResponse(new FriendRequestErrorComposer(2));
            return;
        }

        int id = 0;
        boolean allowFriendRequests = true;

        FriendRequest friendRequest = this.client.getHabbo().getMessenger().findFriendRequest(username);
        if (friendRequest != null)
        {
            this.client.getHabbo().getMessenger().acceptFriendRequest(friendRequest.getId(), this.client.getHabbo().getHabboInfo().getId());
            return;
        }

        if(!Messenger.canFriendRequest(this.client.getHabbo(), username))
        {
            this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
            return;
        }

        if(habbo == null)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users_settings.block_friendrequests, users.id FROM users INNER JOIN users_settings ON users.id = users_settings.user_id WHERE username = ? LIMIT 1"))
            {
                statement.setString(1, username);
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        id = set.getInt("id");
                        allowFriendRequests = set.getString("block_friendrequests").equalsIgnoreCase("0");
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
                return;
            }
        }
        else
        {
            id = habbo.getHabboInfo().getId();
            allowFriendRequests = !habbo.getHabboStats().blockFriendRequests;
            if(allowFriendRequests)
                habbo.getClient().sendResponse(new FriendRequestComposer(this.client.getHabbo()));
        }

        if(id != 0)
        {
            if(!allowFriendRequests)
            {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_ACCEPTING_REQUESTS));
                return;
            }

            if(this.client.getHabbo().getMessenger().getFriends().values().size() >= Messenger.MAXIMUM_FRIENDS)
            {
                this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.FRIEND_LIST_OWN_FULL));
                return;
            }
                Messenger.makeFriendRequest(this.client.getHabbo().getHabboInfo().getId(), id);
        }
        else
        {
            this.client.sendResponse(new FriendRequestErrorComposer(FriendRequestErrorComposer.TARGET_NOT_FOUND));
        }
    }
}
