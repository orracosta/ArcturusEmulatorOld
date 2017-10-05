package com.habboproject.server.network.messages.incoming.group.favourite;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupBadgesMessageComposer;
import com.habboproject.server.network.messages.outgoing.group.UpdateFavouriteGroupMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class SetFavouriteGroupMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();

        if (!client.getPlayer().getGroups().contains(groupId)) {
            return;
        }

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null)
            return;

        client.getPlayer().getData().setFavouriteGroup(groupId);
        client.getPlayer().getData().save();

        if (client.getPlayer().getEntity() != null) {
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new GroupBadgesMessageComposer(groupId, group.getData().getBadge()));

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(client.getPlayer().getEntity().getId()));
            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(client.getPlayer().getEntity()));

            client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarUpdateMessageComposer(client.getPlayer().getEntity()));
        } else {
            client.send(new GroupBadgesMessageComposer(groupId, group.getData().getBadge()));
        }

        client.send(new UpdateFavouriteGroupMessageComposer(client.getPlayer().getId()));
    }
}
