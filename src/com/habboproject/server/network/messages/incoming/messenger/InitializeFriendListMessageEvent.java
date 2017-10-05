package com.habboproject.server.network.messages.incoming.messenger;

import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.messenger.BuddyListMessageComposer;
import com.habboproject.server.network.messages.outgoing.messenger.FriendRequestsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class InitializeFriendListMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.send(new BuddyListMessageComposer(client.getPlayer().getMessenger().getFriends(), client.getPlayer().getPermissions().getRank().messengerStaffChat()));
        client.send(new FriendRequestsMessageComposer(client.getPlayer().getMessenger().getRequestAvatars()));

        if(!client.getPlayer().getAchievements().hasStartedAchievement(AchievementType.FRIENDS_LIST)) {
            client.getPlayer().getAchievements().progressAchievement(AchievementType.FRIENDS_LIST, client.getPlayer().getMessenger().getFriends().size());
        }
    }
}
