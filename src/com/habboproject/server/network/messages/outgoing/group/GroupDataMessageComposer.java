package com.habboproject.server.network.messages.outgoing.group;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;


public class GroupDataMessageComposer extends MessageComposer {
    private final List<Integer> groups;
    private final int playerId;

    public GroupDataMessageComposer(final List<Integer> groups, final int playerId) {
        this.groups = groups;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.GroupFurniConfigMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        int count = 0;

        for (Integer groupId : groups) {
            if (GroupManager.getInstance().getData(groupId) != null) count++;
        }

        msg.writeInt(count);

        for (Integer groupId : groups) {
            GroupData group = GroupManager.getInstance().getData(groupId);

            if (group != null) {
                msg.writeInt(group.getId());
                msg.writeString(group.getTitle());
                msg.writeString(group.getBadge());

                String colourA = GroupManager.getInstance().getGroupItems().getSymbolColours().get(group.getColourA()) != null ? GroupManager.getInstance().getGroupItems().getSymbolColours().get(group.getColourA()).getColour() : "ffffff";
                String colourB = GroupManager.getInstance().getGroupItems().getBackgroundColours().get(group.getColourB()) != null ? GroupManager.getInstance().getGroupItems().getBackgroundColours().get(group.getColourB()).getColour() : "ffffff";

                msg.writeString(colourA);
                msg.writeString(colourB);

                msg.writeBoolean(group.getOwnerId() == this.playerId);
                msg.writeInt(group.getOwnerId());
                msg.writeBoolean(group.hasForum());
            }
        }
    }
}