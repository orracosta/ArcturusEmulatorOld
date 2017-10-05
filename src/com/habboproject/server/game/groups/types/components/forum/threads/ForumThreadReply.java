package com.habboproject.server.game.groups.types.components.forum.threads;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;

public class ForumThreadReply {
    private int id;
    private int index;

    private String message;

    private int threadId;

    private int authorId;
    private int authorTimestamp;

    private int deleterId;
    private int deleterTime;

    private int state;

    public ForumThreadReply(int id, int index, String message, int threadId, int authorId, int authorTimestamp,
                            int state, int deleterId, int deleterTime) {
        this.id = id;
        this.index = index;
        this.message = message;
        this.threadId = threadId;
        this.authorId = authorId;
        this.authorTimestamp = authorTimestamp;
        this.state = state;
        this.deleterId = deleterId;
        this.deleterTime = deleterTime;
    }
    
    public void compose(IComposer msg) {
        final PlayerAvatar replyAuthor = PlayerManager.getInstance().getAvatarByPlayerId(this.getAuthorId(),
                PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(this.getId());
        msg.writeInt(this.index);

        msg.writeInt(this.getAuthorId());
        msg.writeString(replyAuthor.getUsername());
        msg.writeString(replyAuthor.getFigure());

        msg.writeInt((int) Comet.getTime() - this.getAuthorTimestamp());

        msg.writeString(this.getMessage());

        msg.writeByte(this.getState());

        final PlayerAvatar deleterReply = PlayerManager.getInstance().getAvatarByPlayerId(this.getDeleterId(),
                PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(deleterReply == null ? 0 : this.getDeleterId());
        msg.writeString(deleterReply == null ? "" : deleterReply.getUsername());
        msg.writeInt(deleterReply == null ? 0 : (int) Comet.getTime() - this.getDeleterTime());

        msg.writeInt(replyAuthor.getForumPosts()); // messages by author
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getAuthorTimestamp() {
        return authorTimestamp;
    }

    public void setAuthorTimestamp(int authorTimestamp) {
        this.authorTimestamp = authorTimestamp;
    }

    public int getThreadId() {
        return threadId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDeleterId() {
        return deleterId;
    }

    public void setDeleterId(int deleterId) {
        this.deleterId = deleterId;
    }

    public int getDeleterTime() {
        return deleterTime;
    }

    public void setDeleterTime(int deleterTime) {
        this.deleterTime = deleterTime;
    }
}
