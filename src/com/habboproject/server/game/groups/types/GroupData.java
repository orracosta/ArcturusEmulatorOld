package com.habboproject.server.game.groups.types;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.storage.queries.groups.GroupDao;

import java.sql.ResultSet;
import java.sql.SQLException;


public class GroupData {
    /**
     * The ID of the group1
     */
    private int id;

    /**
     * The title of the group
     */
    private String title;

    /**
     * The description of the group
     */
    private String description;

    /**
     * The badge image created by the group owner
     */
    private String badge;

    /**
     * The group owner's ID
     */
    private int ownerId;

    /**
     * The room assigned to the group's ID
     */
    private int roomId;

    /**
     * The time the group was created
     */
    private int created;

    /**
     * The type of the group.. Can members freely join or do
     * they need to request membership?
     */
    private GroupType type;

    /**
     * The first colour of the group
     */
    private int colourA;

    /**
     * The second colour of the group
     */
    private int colourB;

    /**
     * Can members of the group decorate the room
     * assigned to it?
     */
    private boolean canMembersDecorate;

    /**
     * Does the group have a forum?
     */
    private boolean hasForum;

    /**
     * Load the group data straight from the database
     *
     * @param data The set of data from the database
     * @throws SQLException
     */
    public GroupData(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.title = data.getString("name");
        this.description = data.getString("description");
        this.badge = data.getString("badge");
        this.ownerId = data.getInt("owner_id");
        this.created = data.getInt("created");
        this.roomId = data.getInt("room_id");
        this.type = GroupType.valueOf(data.getString("type").toUpperCase());
        this.colourA = data.getInt("colour1");
        this.colourB = data.getInt("colour2");
        this.canMembersDecorate = data.getString("members_deco").equals("1");
        this.hasForum = data.getString("has_forum").equals("1");
    }

    /**
     * Create a group data instance using data provided by
     * the group creator
     *
     * @param title       The title of the group
     * @param description The description of the group
     * @param badge       The badge created by the group owner
     * @param ownerId     The ID of the user
     * @param roomId      The room ID assigned to the group
     * @param colourA     The first colour of the group
     * @param colourB     The second colour of the group
     */
    public GroupData(String title, String description, String badge, int ownerId, int roomId, int colourA, int colourB) {
        this.id = -1;
        this.title = title;
        this.description = description;
        this.badge = badge.replace("s00000", "");
        this.ownerId = ownerId;
        this.roomId = roomId;
        this.created = (int) Comet.getTime();
        this.type = GroupType.REGULAR;
        this.colourA = colourA;
        this.colourB = colourB;
        this.canMembersDecorate = false;
    }

    /**
     * Save the group data to the database
     * TODO: Queue saving
     */
    public void save() {
        GroupDao.save(this);
    }

    /**
     * Get the ID of the group
     *
     * @return The ID of the group
     */
    public int getId() {
        return this.id;
    }

    /**
     * Set the ID of the group
     *
     * @param id The ID of the group
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the title of the group
     *
     * @return The title of the group
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set the title of the group
     *
     * @param title The title of the group
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the description of the group
     *
     * @return The description of the group
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the description of the group
     *
     * @param description The description of the group
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the ID of the owner of the group
     *
     * @return The ID of the owner of the group
     */
    public int getOwnerId() {
        return this.ownerId;
    }

    /**
     * Set the ID of the owner of the group
     *
     * @param id The ID of the group
     */
    public void setOwnerId(int id) {
        this.ownerId = id;
    }

    /**
     * Get the badge of the group
     *
     * @return The badge created by the group owner
     */
    public String getBadge() {
        return badge;
    }

    /**
     * Set the badge of the group
     *
     * @param badge The badge created by the group owner
     */
    public void setBadge(String badge) {
        this.badge = badge.replace("s00000", "");
    }

    /**
     * Get the room ID assigned the group
     *
     * @return The room ID assigned to the group
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Set the room ID assigned to the group
     *
     * @param roomId The room ID assigned to the group
     */
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    /**
     * Get the time the group was created
     *
     * @return The time the group was created
     */
    public int getCreatedTimestamp() {
        return created;
    }

    /**
     * Can the group members decorate the room?
     *
     * @return Can the group members decorate the room?
     */
    public boolean canMembersDecorate() {
        return canMembersDecorate;
    }

    /**
     * Set whether or not the group administrators can decorate the room
     *
     * @param canMembersDecorate Can the group administrators decorate the room?
     */
    public void setCanMembersDecorate(boolean canMembersDecorate) {
        this.canMembersDecorate = canMembersDecorate;
    }

    /**
     * Get the membership type of the group
     *
     * @return The membership type of the group
     */
    public GroupType getType() {
        return type;
    }

    /**
     * Set the membership type of the group
     *
     * @param type The membership type of the group
     */
    public void setType(GroupType type) {
        this.type = type;
    }

    /**
     * Get the first colour of the group
     *
     * @return The first colour of the group
     */
    public int getColourA() {
        return colourA;
    }

    /**
     * Set the first colour of the group
     *
     * @param colourA The first colour of the group
     */
    public void setColourA(int colourA) {
        this.colourA = colourA;
    }

    /**
     * Get the second colour of the group
     *
     * @return The second colour of the group
     */
    public int getColourB() {
        return colourB;
    }

    /**
     * Set the second colour of the group
     *
     * @param colourB The second colour of the group
     */
    public void setColourB(int colourB) {
        this.colourB = colourB;
    }

    /**
     * Does the group have a forum?
     *
     * @return Whether or not the group has a forum
     */
    public boolean hasForum() {
        return this.hasForum;
    }

    public void setHasForum(boolean hasForum) {
        this.hasForum = hasForum;
    }
}
