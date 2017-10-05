package com.habboproject.server.network.messages.incoming.group.forum.data;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.forums.GroupForumListMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GetForumsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int currentTab = msg.readInt();
        int startIndex = msg.readInt();
        int endIndex = msg.readInt();

        client.send(new GroupForumListMessageComposer(currentTab, startIndex, this.getGroupsByTab(client, currentTab),
                client.getPlayer().getId()));
    }

    private List<Group> getGroupsByTab(Session client, int currentTab) {
        switch (currentTab) {
            case 0: {
                List<Group> groups = Lists.newArrayList();

                groups.addAll(GroupManager.getInstance().getGroupForumIntances().values().stream().filter(x -> x != null &&
                        x.getForumComponent().threadsSize() > 0).collect(Collectors.toList()));

                try {
                    Collections.sort(groups, new Comparator<Group>() {
                        @Override
                        public int compare(Group o1, Group o2) {
                            Integer i = o1.getForumComponent().repliesSize();
                            Integer j = o2.getForumComponent().repliesSize();
                            return j - i;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return groups;
            }

            case 1: {
                List<Group> groups = Lists.newArrayList();

                groups.addAll(GroupManager.getInstance().getGroupForumIntances().values());

                try {
                    Collections.sort(groups, new Comparator<Group>() {
                        @Override
                        public int compare(Group o1, Group o2) {
                            Integer i = o1.getForumComponent().playerViews();
                            Integer j = o2.getForumComponent().playerViews();
                            return j - i;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return groups;
            }

            case 2: {
                List<Group> groups = Lists.newArrayList();

                for (Integer groupId : client.getPlayer().getGroups()) {
                    GroupData groupData = GroupManager.getInstance().getData(groupId);
                    if (groupData != null && groupData.hasForum()) {
                        groups.add(GroupManager.getInstance().get(groupId));
                    }
                }


                return groups;
            }
        }

        return Lists.newArrayList();
    }
}
