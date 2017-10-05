package com.habboproject.server.game.groups.types.components.membership;

import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.groups.types.components.GroupComponent;
import com.habboproject.server.storage.queries.groups.GroupMemberDao;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MembershipComponent implements GroupComponent {
    /**
     * The instance of the group
     */
    private Group group;

    /**
     * All members of this group
     */
    private Map<Integer, GroupMember> groupMembers;

    /**
     * Administrators of this group
     */
    private Set<Integer> groupAdministrators;

    /**
     * The IDs of players who are still not accepted to join
     */
    private Set<Integer> groupMembershipRequests;

    /**
     * Initialize the MembershipComponent
     *
     * @param group The ID of the group
     */
    public MembershipComponent(Group group) {
        this.group = group;

        this.groupMembers = new ListOrderedMap<>();
        this.groupAdministrators = new ListOrderedSet<>();
        this.groupMembershipRequests = new ListOrderedSet<>();

        this.loadMemberships();
    }

    /**
     * Load members of this group from the database
     */
    private void loadMemberships() {
        for (GroupMember groupMember : GroupMemberDao.getAllByGroupId(this.group.getId())) {
            this.createMembership(groupMember);
        }

        for (Integer playerId : GroupMemberDao.getAllRequestsByGroupId(this.group.getId())) {
            this.groupMembershipRequests.add(playerId);
        }
    }

    /**
     * Add a new member to the group
     *
     * @param groupMember The new member
     */
    public void createMembership(GroupMember groupMember) {
        if (groupMember.getMembershipId() == 0)
            groupMember.setMembershipId(GroupMemberDao.create(groupMember));

        if (groupMembers.containsKey(groupMember.getPlayerId()))
            groupMembers.remove(groupMember.getPlayerId());

        if (groupMember.getAccessLevel().isAdmin())
            this.groupAdministrators.add(groupMember.getPlayerId());

        groupMembers.put(groupMember.getPlayerId(), groupMember);
    }

    /**
     * Remove a player's membership to the group
     *
     * @param playerId The ID of the player to remove
     */
    public void removeMembership(int playerId) {
        if (!groupMembers.containsKey(playerId))
            return;

        int groupMembershipId = groupMembers.get(playerId).getMembershipId();

        GroupMemberDao.delete(groupMembershipId);

        groupMembers.remove(playerId);

        if (groupAdministrators.contains(playerId))
            groupAdministrators.remove(playerId);
    }

    /**
     * Add a new membership request to the group
     *
     * @param playerId The ID of the player who is requesting to join
     */
    public void createRequest(int playerId) {
        if (groupMembers.containsKey(playerId))
            return;

        if (groupMembershipRequests.contains(playerId))
            return;

        groupMembershipRequests.add(playerId);
        GroupMemberDao.createRequest(this.group.getId(), playerId);
    }

    /**
     * Clears all membership requests
     */
    public void clearRequests() {
        if (groupMembershipRequests.size() == 0)
            return;

        groupMembershipRequests.clear();
        GroupMemberDao.clearRequests(this.group.getId());
    }

    /**
     * Removes membership request
     */
    public void removeRequest(int playerId) {
        if (!groupMembershipRequests.contains(playerId))
            return;

        groupMembershipRequests.remove(playerId);

        GroupMemberDao.deleteRequest(this.group.getId(), playerId);
    }

    /**
     * Clears all lists associated with this object
     */
    @Override
    public void dispose() {
        groupMembers.clear();
        groupAdministrators.clear();
        groupMembershipRequests.clear();
    }

    /**
     * Get the members of the group
     *
     * @return The members of the group
     */
    public Map<Integer, GroupMember> getMembers() {
        return groupMembers;
    }

    /**
     * Get the members of the group in a list
     *
     * @return The members of the group in a list
     */
    public List<GroupMember> getMembersAsList() {
        List<GroupMember> groupMembers = new ArrayList<>();

        for (GroupMember groupMember : this.getMembers().values()) {
            groupMembers.add(groupMember);
        }

        return groupMembers;
    }

    /**
     * Get the administrators of the group
     *
     * @return The administrators of the group
     */
    public Set<Integer> getAdministrators() {
        return groupAdministrators;
    }

    /**
     * Get the membership requests of the group
     *
     * @return The membership requests of the group
     */
    public Set<Integer> getMembershipRequests() {
        return groupMembershipRequests;
    }

    /**
     * Get the group that this component is assigned to
     *
     * @return The group that this component is assigned to
     */
    @Override
    public Group getGroup() {
        return this.group;
    }
}
