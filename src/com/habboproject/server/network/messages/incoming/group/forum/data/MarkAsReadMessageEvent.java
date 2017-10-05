package com.habboproject.server.network.messages.incoming.group.forum.data;

import com.google.common.collect.Lists;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.util.List;

/**
 * Created by brend on 02/03/2017.
 */
public class MarkAsReadMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int amountOfForums = msg.readInt();

        List<Integer> groupIds = Lists.newArrayList();
        for (int i = 0; i < amountOfForums; i++) {
            int groupId = msg.readInt();
            int unknowInt = msg.readInt();
            if (amountOfForums > 1) {
                boolean unknowBool = msg.readBoolean();
            }

            if (groupId > 0) {
                groupIds.add(groupId);
            }
        }

        for (Integer groupId : groupIds) {
            Group group = GroupManager.getInstance().get(groupId);
            if (group != null && group.getData().hasForum()) {
                group.getForumComponent().markAsRead(client.getPlayer().getId(),
                        (int) Comet.getTime());
            }
        }
    }
}
