package com.habboproject.server.game.groups;

import com.google.common.collect.Maps;
import com.habboproject.server.game.groups.items.GroupItemManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.storage.queries.groups.GroupDao;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class GroupManager implements Initializable {
    private static GroupManager instance;

    private GroupItemManager groupItems;

    private Map<Integer, GroupData> groupDatas;

    private Map<Integer, Group> groupInstances;
    private Map<Integer, Group> groupForumIntances;

    private Map<Integer, Integer> roomIdToGroupId;

    private Logger log = Logger.getLogger(GroupManager.class.getName());

    public GroupManager() {

    }

    @Override
    public void initialize() {
        this.groupItems = new GroupItemManager();

        this.groupDatas = Maps.newConcurrentMap();
        this.groupInstances = Maps.newConcurrentMap();
        this.groupForumIntances = Maps.newConcurrentMap();
        this.roomIdToGroupId = Maps.newConcurrentMap();

        this.loadForums();

        log.info("GroupManager initialized");
    }

    private void loadForums() {
        if (this.groupForumIntances.size() > 0) {
            this.groupForumIntances.clear();
        }

        List<Integer> forums = GroupDao.loadForums();
        for (Integer groupId : forums) {
            this.groupForumIntances.put(groupId, this.get(groupId));
        }
    }

    public static GroupManager getInstance() {
        if (instance == null) {
            instance = new GroupManager();
        }

        return instance;
    }

    public GroupData getData(int id) {
        if (this.groupDatas.get(id) != null)
            return this.groupDatas.get(id);

        GroupData groupData = GroupDao.getDataById(id);

        if (groupData != null)
            this.groupDatas.put(id, groupData);

        return groupData;
    }

    public Group get(int id) {
        if(id <= 0) {
            // speed speed
            return null;
        }

        Group groupInstance = this.groupInstances.get(id);

        if (groupInstance != null)
            return groupInstance;

        if (this.getData(id) == null) {
            return null;
        }

        groupInstance = this.load(id);

        this.groupInstances.put(id, groupInstance);

        log.trace("Group with id #" + id + " was loaded");

        return groupInstance;
    }

    public Group createGroup(GroupData groupData) {
        int groupId = GroupDao.create(groupData);

        groupData.setId(groupId);
        this.groupDatas.put(groupId, groupData);

        Group groupInstance = new Group(groupId);
        this.groupInstances.put(groupId, groupInstance);

        return groupInstance;
    }

    public void addForum(Group group) {
        this.groupForumIntances.put(group.getId(), group);
    }

    public Group getGroupByRoomId(int roomId) {
        // TODO: Optimize this.
        if (this.roomIdToGroupId.containsKey(roomId))
            return this.get(roomIdToGroupId.get(roomId));

        int groupId = GroupDao.getIdByRoomId(roomId);

        if (groupId != 0)
            this.roomIdToGroupId.put(roomId, groupId);

        return this.get(groupId);
    }

    public void removeGroup(int id) {
        Group group = this.get(id);

        if (group == null)
            return;

        if (this.roomIdToGroupId.containsKey(group.getData().getRoomId())) {
            this.roomIdToGroupId.remove(group.getData().getRoomId());
        }

        this.groupInstances.remove(id);
        this.groupDatas.remove(id);

        group.getMembershipComponent().dispose();

        GroupDao.deleteGroup(group.getId());
    }

    private Group load(int id) {
        return new Group(id);
    }

    public GroupItemManager getGroupItems() {
        return groupItems;
    }

    public Map<Integer, GroupData> getGroupDatas() {
        return groupDatas;
    }

    public Map<Integer, Group> getGroupInstances() {
        return groupInstances;
    }

    public Map<Integer, Group> getGroupForumIntances() {
        return groupForumIntances;
    }

    public Logger getLogger() {
        return log;
    }
}
