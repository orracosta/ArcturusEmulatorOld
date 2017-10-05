package com.habboproject.server.network.messages.outgoing.group.forums;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThreadReply;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;

public class GroupForumViewThreadMessageComposer extends MessageComposer {
    private GroupData groupData;
    private final int threadId;
    private List<ForumThreadReply> replies;
    private int startIndex;

    public GroupForumViewThreadMessageComposer(GroupData groupData, int threadId, List<ForumThreadReply> threadReplies, int startIndex) {
        this.groupData = groupData;
        this.threadId = threadId;
        this.replies = threadReplies;
        this.startIndex = startIndex;
    }

    @Override
    public short getId() {
        return Composers.ThreadDataMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.groupData.getId());
        msg.writeInt(this.threadId);
        msg.writeInt(this.startIndex);
        msg.writeInt(this.replies.size());

        for(ForumThreadReply reply : this.replies) {
            reply.compose(msg);
        }
    }
}
