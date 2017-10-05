package com.habboproject.server.network.messages.incoming.group.favourite;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.UpdateFavouriteGroupMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class ClearFavouriteGroupMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        client.getPlayer().getData().setFavouriteGroup(0);
        client.getPlayer().getData().save();

        if (client.getPlayer().getEntity() != null) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(client.getPlayer().getEntity().getId()));
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(client.getPlayer().getEntity()));

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(client.getPlayer().getEntity()));
        }

        client.send(new UpdateFavouriteGroupMessageComposer(client.getPlayer().getId()));
    }
}
