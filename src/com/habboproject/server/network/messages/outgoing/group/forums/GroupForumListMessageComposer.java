package com.habboproject.server.network.messages.outgoing.group.forums;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;
import java.util.stream.Collectors;

public class GroupForumListMessageComposer extends MessageComposer {
    private final int currentTab;
    private final int startIndex;

    private final List<Group> groups;

    private final int playerId;

    public GroupForumListMessageComposer(int currentTab, int startIndex, List<Group> groups, int playerId) {
        this.currentTab = currentTab;
        this.startIndex = startIndex;
        this.groups = groups;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.ForumsListDataMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        List<Group> groupsByIndex = this.groups.stream().skip(this.startIndex).limit(20).collect(Collectors.toList());

        msg.writeInt(this.currentTab);
        msg.writeInt(this.groups.size());
        msg.writeInt(this.startIndex);

        msg.writeInt(groupsByIndex.size());
        for (Group group : groupsByIndex) {
            group.getForumComponent().composeData(playerId, msg);
        }
    }
}
