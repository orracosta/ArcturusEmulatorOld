package com.habboproject.server.network.messages.incoming.group;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class GroupInformationMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        boolean flag = msg.readBoolean();

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null)
            return;

        client.send(group.composeInformation(flag, client.getPlayer().getId()));
    }
}
