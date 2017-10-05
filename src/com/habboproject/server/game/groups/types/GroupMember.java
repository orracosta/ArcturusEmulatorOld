package com.habboproject.server.game.groups.types;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.storage.queries.groups.GroupMemberDao;

import java.sql.ResultSet;
import java.sql.SQLException;


public class GroupMember {
    /**
     * The ID of the membership
     */
    private int membershipId;

    /**
     * The ID of the player
     */
    private int playerId;

    /**
     * The ID of the group
     */
    private int groupId;

    /**
     * The level of the user
     */
    private GroupAccessLevel accessLevel;

    /**
     * The date the user joined the group
     */
    private int dateJoined;

    /**
     * Initialize the member object with data from the database
     *
     * @param data Data from the database
     * @throws SQLException
     */
    public GroupMember(ResultSet data) throws SQLException {
        this.membershipId = data.getInt("id");
        this.playerId = data.getInt("player_id");
        this.accessLevel = GroupAccessLevel.valueOf(data.getString("access_level").toUpperCase());
        this.groupId = data.getInt("group_id");
        this.dateJoined = data.getInt("date_joined");
    }

    /**
     * Initialize the member object before it's saved to the database
     *
     * @param playerId    ID of the player
     * @param groupId     ID of the group
     * @param accessLevel The level of access the member has
     */
    public GroupMember(int playerId, int groupId, GroupAccessLevel accessLevel) {
        this.membershipId = 0;
        this.playerId = playerId;
        this.groupId = groupId;
        this.accessLevel = accessLevel;
        this.dateJoined = (int) Comet.getTime();
    }

    /**
     * Get the ID of the membership
     *
     * @return The ID of the membership
     */
    public int getMembershipId() {
        return membershipId;
    }

    /**
     * Set the ID of the membership
     *
     * @param membershipId The ID of the membership1
     */
    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    /**
     * Get the ID of the member
     *
     * @return Get the ID of the member
     */
    public int getPlayerId() {
        return this.playerId;
    }

    public int getGroupId() {
        return groupId;
    }

    /**
     * Get the level of access the member has
     *
     * @return The level of access the member has
     */
    public GroupAccessLevel getAccessLevel() {
        return this.accessLevel;
    }

    /**
     * Set the level of access the member has
     *
     * @param accessLevel The level of access the member has
     */
    public void setAccessLevel(GroupAccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Get the date the user joined the group
     *
     * @return The date the user joined the group
     */
    public int getDateJoined() {
        return dateJoined;
    }

    /**
     * Save this membership object to the database
     */
    public void save() {
        GroupMemberDao.save(this);
    }
}
