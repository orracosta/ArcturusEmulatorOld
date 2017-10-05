package com.habboproject.server.game.groups.types.components.forum.threads;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ForumThread {
    private int id;
    private String title;

    private int authorId;
    private int authorTimestamp;

    private int state;

    private boolean isLocked;
    private boolean isPinned;

    private int deleterId;
    private int deleterTime;

    private List<ForumThreadReply> replies;

    public ForumThread(int id, String title, String message, int authorId, int authorTimestamp, int state,
                       boolean isLocked, boolean isPinned, int deleterId, int deleterTime) {
        this.id = id;
        this.title = title;

        this.authorId = authorId;
        this.authorTimestamp = authorTimestamp;

        this.state = state;

        this.isLocked = isLocked;
        this.isPinned = isPinned;

        this.deleterId = deleterId;
        this.deleterTime = deleterTime;

        this.replies = new ArrayList<>();

        this.replies.add(new ForumThreadReply(id, 0, message, id, authorId, authorTimestamp, state,
                deleterId, deleterTime));
    }

    public void compose(int unreadMessages, IComposer msg) {
        msg.writeInt(this.getId());

        final PlayerAvatar threadAuthor = PlayerManager.getInstance().getAvatarByPlayerId(this.getAuthorId(), PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(threadAuthor == null ? 0 : threadAuthor.getId());
        msg.writeString(threadAuthor == null ? "Unknown Player" : threadAuthor.getUsername());

        msg.writeString(this.getTitle());

        msg.writeBoolean(this.isPinned());
        msg.writeBoolean(this.isLocked());

        msg.writeInt((int) Comet.getTime() - this.getAuthorTimestamp());

        msg.writeInt(this.getReplies().size());

        msg.writeInt(unreadMessages);

        final PlayerAvatar replyAuthor = PlayerManager.getInstance().getAvatarByPlayerId(this.getMostRecentPost().getAuthorId(), PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(replyAuthor == null ? 0 :this.getMostRecentPost().getId());
        msg.writeInt(replyAuthor == null ? 0 : replyAuthor.getId());
        msg.writeString(replyAuthor == null ? "Unknown Player" : replyAuthor.getUsername());
        msg.writeInt(replyAuthor == null ? 0 : (int) Comet.getTime() - this.getMostRecentPost().getAuthorTimestamp());

        msg.writeByte(this.getState());

        final PlayerAvatar threadDeleter = PlayerManager.getInstance().getAvatarByPlayerId(this.getDeleterId(), PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(threadDeleter == null ? 0 : this.getDeleterId());
        msg.writeString(threadDeleter == null ? "" : threadDeleter.getUsername());
        msg.writeInt(threadDeleter == null ? 0 : (int) Comet.getTime() - this.getDeleterTime());
    }

    public List<ForumThreadReply> getReplies(int startIndex) {
        List<ForumThreadReply> replies = Lists.newArrayList();

        for (ForumThreadReply reply : this.getReplies()) {
            replies.add(reply);
        }


        return replies.stream().skip(startIndex).limit(20).collect(Collectors.toList());
    }

    public ForumThreadReply getReplyById(final int id) {
        for(ForumThreadReply reply : this.replies) {
            if(reply.getId() == id) {
                return reply;
            }
        }

        return null;
    }

    public ForumThreadReply getMostRecentPost() {
        return this.replies.get(this.replies.size() - 1);
    }

    public void addReply(ForumThreadReply reply) {
        this.replies.add(reply);
    }

    public void dispose() {
        this.replies.clear();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ForumThreadReply> getReplies() {
        return replies;
    }

    public void setReplies(List<ForumThreadReply> replies) {
        this.replies = replies;
    }

    public int getAuthorId() {
        return authorId;
    }

    public int getAuthorTimestamp() {
        return authorTimestamp;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
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
