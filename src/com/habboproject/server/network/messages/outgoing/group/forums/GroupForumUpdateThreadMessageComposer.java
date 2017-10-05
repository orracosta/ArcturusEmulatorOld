package com.habboproject.server.network.messages.outgoing.group.forums;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class GroupForumUpdateThreadMessageComposer extends MessageComposer {
    private int groupId;
    private ForumThread forumThread;
    private int playerId;

    public GroupForumUpdateThreadMessageComposer(int groupId, ForumThread forumThread, int playerId) {
        this.groupId = groupId;
        this.forumThread = forumThread;
        this.playerId = playerId;
    }

    @Override
    public short getId() {
        return Composers.ThreadUpdatedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        Group group = GroupManager.getInstance().get(this.groupId);

        if (group != null) {
            msg.writeInt(this.groupId);

            this.forumThread.compose(group.getForumComponent().getUnreadThreadReplies(this.playerId, this.forumThread.getId()),
                    msg);
        }
    }
}
