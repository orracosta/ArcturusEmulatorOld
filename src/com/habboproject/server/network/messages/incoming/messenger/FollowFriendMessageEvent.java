package com.habboproject.server.network.messages.incoming.messenger;

import com.habboproject.server.game.players.components.types.messenger.MessengerFriend;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class FollowFriendMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        int friendId = msg.readInt();

        MessengerFriend friend = client.getPlayer().getMessenger().getFriendById(friendId);

        if (friend == null || !friend.isInRoom())
            return;

        Room room = friend.getSession().getPlayer().getEntity().getRoom();

        if (room == null) {
            // wtf?
            return;
        }

        if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom() != null) {
            Room roomInstance = client.getPlayer().getEntity().getRoom();

            if (roomInstance.getId() == room.getId()) {
                client.getPlayer().getEntity().leaveRoom(false, false, false);
            }
        }

        client.send(new RoomForwardMessageComposer(room.getId()));
    }
}