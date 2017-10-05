package com.habboproject.server.game.groups.types.components.forum;

import com.google.common.collect.Maps;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.components.GroupComponent;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumSettings;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThreadReply;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.storage.queries.groups.GroupForumThreadDao;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForumComponent implements GroupComponent {
    public static final int MAX_MESSAGES_PER_PAGE = 20;

    private Group group;

    private ForumSettings forumSettings;

    private List<Integer> pinnedThreads;
    private Map<Integer, ForumThread> forumThreads;
    private Map<Integer, Map<Integer, Integer>> playerViews;

    public ForumComponent(Group group, ForumSettings forumSettings) {
        this.group = group;
        this.forumSettings = forumSettings;
        this.forumThreads = GroupForumThreadDao.getAllMessagesForGroup(this.group.getId());
        this.playerViews = GroupForumThreadDao.getAllPlayerViewsForGroup(this.group.getId());
        this.pinnedThreads = Lists.newArrayList();

        for (ForumThread forumThread : forumThreads.values()) {
            if (forumThread.isPinned()) {
                this.pinnedThreads.add(forumThread.getId());
            }
        }
    }

    public int playerViews() {
        return this.playerViews.size();
    }

    public int threadsSize() {
        return this.forumThreads.size();
    }

    public int repliesSize() {
        int replies = 0;
        for (ForumThread thread : this.forumThreads.values()) {
            replies += thread.getReplies().size();
        }
        return replies;
    }

    public double getScore() {
        double score = 0.0;
        for (int i = 0; i < this.threadsSize(); i++) {
            score += 0.25;
        }

        return score;
    }

    public void addView(int threadId, int playerId, int time) {
        if (!this.playerViews.containsKey(threadId)) {
            this.playerViews.put(threadId, Maps.newHashMap());
            this.playerViews.get(threadId).put(playerId, time);

            GroupForumThreadDao.registerPlayerView(this.group.getId(), threadId, playerId, time);

            return;
        }

        if (!this.playerViews.get(threadId).containsKey(playerId)) {
            this.playerViews.get(threadId).put(playerId, time);

            GroupForumThreadDao.registerPlayerView(this.group.getId(), threadId, playerId, time);

            return;
        }

        this.playerViews.get(threadId).remove(playerId);
        this.playerViews.get(threadId).put(playerId, time);

        GroupForumThreadDao.updatePlayerView(this.group.getId(), threadId, playerId, time);
    }

    public void markAsRead(int playerId, int time) {
        for (ForumThread thread : this.forumThreads.values()) {
            if (thread == null)
                continue;

            if (!this.playerViews.containsKey(thread.getId())) {
                this.playerViews.put(thread.getId(), Maps.newHashMap());
                this.playerViews.get(thread.getId()).put(playerId, time);

                GroupForumThreadDao.registerPlayerView(this.group.getId(), thread.getId(), playerId, time);

                continue;
            }

            if (!this.playerViews.get(thread.getId()).containsKey(playerId)) {
                this.playerViews.get(thread.getId()).put(playerId, time);

                GroupForumThreadDao.registerPlayerView(this.group.getId(), thread.getId(), playerId, time);

                continue;
            }

            this.playerViews.get(thread.getId()).remove(playerId);
            this.playerViews.get(thread.getId()).put(playerId, time);

            GroupForumThreadDao.updatePlayerView(this.group.getId(), thread.getId(), playerId, time);
        }
    }

    public int getUnreadThreads(int playerId) {
        int unreadThreads = 0;
        for (ForumThread thread : this.forumThreads.values()) {
            if (!this.playerViews.containsKey(thread.getId()) ||
                    !this.playerViews.get(thread.getId()).containsKey(playerId)) {
                unreadThreads += thread.getReplies().size();

                continue;
            }

            long lastView = this.playerViews.get(thread.getId()).get(playerId);

            for (ForumThreadReply reply : thread.getReplies()) {
                if (reply.getAuthorTimestamp() <= lastView)
                    continue;

                ++unreadThreads;
            }
        }

        return unreadThreads;
    }

    public int getUnreadThreadReplies(int playerId, int threadId) {
        int unreadReplies = 0;

        ForumThread thread = this.forumThreads.get(threadId);

        if (thread != null) {
            if (!this.playerViews.containsKey(threadId) || !this.playerViews.get(threadId).containsKey(playerId)) {
                return thread.getReplies().size();
            }

            long lastView = this.playerViews.get(threadId).get(playerId);

            for (ForumThreadReply reply : thread.getReplies()) {
                if (reply.getAuthorTimestamp() <= lastView)
                    continue;

                ++unreadReplies;
            }
        }

        return unreadReplies;
    }

    public void composeData(int playerId, IComposer msg) {
        msg.writeInt(group.getId());

        msg.writeString(group.getData().getTitle());
        msg.writeString(group.getData().getDescription());
        msg.writeString(group.getData().getBadge());

        msg.writeInt(this.threadsSize());
        msg.writeInt((int)Math.abs(this.getScore()));
        msg.writeInt(this.repliesSize());

        msg.writeInt(this.getUnreadThreads(playerId));

        if (this.getMostRecentPost() == null) {
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeString("");
            msg.writeInt(0);
            return;
        }

        final PlayerAvatar lastPlayer = PlayerManager.getInstance().getAvatarByPlayerId(this.getMostRecentPost().getAuthorId(),
                PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(this.getMostRecentPost().getId());

        msg.writeInt(lastPlayer == null ? 0 : lastPlayer.getId());
        msg.writeString(lastPlayer == null ? "Unknow Player" : lastPlayer.getUsername());
        msg.writeInt(lastPlayer == null ? 0 : (int) Comet.getTime() - this.getMostRecentPost().getAuthorTimestamp());
    }

    public List<ForumThread> getForumThreads(int startIndex) {
        ArrayList<ForumThread> threads = Lists.newArrayList();

        for (Integer threadId : this.getPinnedThreads()) {
            ForumThread forumThread = null;
            if ((forumThread = this.getForumThreads().get(threadId)) != null) {
                threads.add(forumThread);
            }
        }

        for (ForumThread forumThread : this.group.getForumComponent().getForumThreads().values()) {
            if (!forumThread.isPinned()) {
                threads.add(forumThread);
            }
        }

        return threads.stream().skip(startIndex).limit(20).collect(Collectors.toList());
    }

    public ForumThreadReply getMostRecentPost() {
        Iterator<Map.Entry<Integer, ForumThread>> threads = this.forumThreads.entrySet().iterator();

        ForumThread lastThread = null;
        while (threads.hasNext()) {
            lastThread = threads.next().getValue();
        }

        if (lastThread == null) {
            return null;
        }

        return lastThread.getMostRecentPost();
    }

    @Override
    public void dispose() {
        for (ForumThread forumThread : this.forumThreads.values()) {
            forumThread.dispose();
        }

        this.forumThreads.clear();
        this.pinnedThreads.clear();
        this.playerViews.clear();
    }

    @Override
    public Group getGroup() {
        return this.group;
    }

    public ForumSettings getForumSettings() {
        return this.forumSettings;
    }

    public Map<Integer, ForumThread> getForumThreads() {
        return forumThreads;
    }

    public List<Integer> getPinnedThreads() {
        return pinnedThreads;
    }
}
