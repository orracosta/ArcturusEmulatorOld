package com.habboproject.server.network.messages.incoming.group.settings;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.groups.GroupElementsMessageComposer;
import com.habboproject.server.network.messages.outgoing.group.ManageGroupMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class ManageGroupMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null || client.getPlayer().getId() != group.getData().getOwnerId())
            return;

        client.send(new GroupElementsMessageComposer()); // TODO: Send this once
        client.send(new ManageGroupMessageComposer(group));
    }
}
