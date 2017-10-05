package com.habboproject.server.game.groups.types.components.forum.settings;

import com.habboproject.server.storage.queries.groups.GroupForumDao;

public class ForumSettings {
    private int groupId;

    private ForumPermission readPermission;
    private ForumPermission postPermission;
    private ForumPermission startThreadsPermission;
    private ForumPermission moderatePermission;

    public ForumSettings(int groupId, ForumPermission readPermission, ForumPermission postPermission,
                         ForumPermission startThreadsPermission, ForumPermission moderatePermission) {
        this.groupId = groupId;
        this.readPermission = readPermission;
        this.postPermission = postPermission;
        this.startThreadsPermission = startThreadsPermission;
        this.moderatePermission = moderatePermission;
    }

    public void save() {
        GroupForumDao.saveSettings(this);
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public ForumPermission getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(ForumPermission readPermission) {
        this.readPermission = readPermission;
    }

    public ForumPermission getPostPermission() {
        return postPermission;
    }

    public void setPostPermission(ForumPermission postPermission) {
        this.postPermission = postPermission;
    }

    public ForumPermission getStartThreadsPermission() {
        return startThreadsPermission;
    }

    public void setStartThreadsPermission(ForumPermission startThreadsPermission) {
        this.startThreadsPermission = startThreadsPermission;
    }

    public ForumPermission getModeratePermission() {
        return moderatePermission;
    }

    public void setModeratePermission(ForumPermission moderatePermission) {
        this.moderatePermission = moderatePermission;
    }
}
