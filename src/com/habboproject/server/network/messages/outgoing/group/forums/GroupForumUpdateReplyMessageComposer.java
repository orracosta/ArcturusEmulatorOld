package com.habboproject.server.network.messages.outgoing.group.forums;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThreadReply;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class GroupForumUpdateReplyMessageComposer extends MessageComposer {
    private final int groupId;
    private final int threadId;
    private final ForumThreadReply reply;

    public GroupForumUpdateReplyMessageComposer(ForumThreadReply reply, int threadId, int groupId) {
        this.reply = reply;
        this.threadId = threadId;
        this.groupId = groupId;
    }

    @Override
    public short getId() {
        return Composers.PostUpdatedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.groupId);
        msg.writeInt(this.threadId);

        this.reply.compose(msg);
    }
}
