package com.habboproject.server.game.groups.types;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.components.forum.ForumComponent;
import com.habboproject.server.game.groups.types.components.forum.settings.ForumSettings;
import com.habboproject.server.game.groups.types.components.membership.MembershipComponent;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.messages.outgoing.group.GroupInformationMessageComposer;
import com.habboproject.server.storage.queries.groups.GroupForumDao;

public class Group {
    /**
     * The ID of the group
     */
    private int id;

    /**
     * The component which will handle everything member-related
     */
    private MembershipComponent membershipComponent;

    /**
     * The component which will handle the group forum data
     */
    private ForumComponent forumComponent;

    /**
     * Initialize the group instance
     *
     * @param id The ID of the group
     */
    public Group(int id) {
        this.id = id;

        this.membershipComponent = new MembershipComponent(this);

        if (this.getData().hasForum()) {
            this.initializeForum();
        }
    }

    /**
     * Create a packet containing group information
     *
     * @param flag     Flag sent by the client (Unknown right now...)
     * @param playerId The ID of the player to receive this message
     * @return Packet containing the group information
     */
    public MessageComposer composeInformation(boolean flag, int playerId) {
        return new GroupInformationMessageComposer(this, RoomManager.getInstance().getRoomData(this.getData().getRoomId()), flag, playerId == this.getData().getOwnerId(), this.getMembershipComponent().getAdministrators().contains(playerId),
                this.getMembershipComponent().getMembers().containsKey(playerId) ? 1 : this.getMembershipComponent().getMembershipRequests().contains(playerId) ? 2 : 0);
    }

    public void initializeForum() {
        ForumSettings forumSettings = GroupForumDao.getSettings(this.id);

        if (forumSettings == null) {
            forumSettings = GroupForumDao.createSettings(this.id);
        }

        this.forumComponent = new ForumComponent(this, forumSettings);
    }

    public void dispose() {
        if (this.membershipComponent != null) {
            this.membershipComponent.dispose();
        }

        if (this.forumComponent != null) {
            this.forumComponent.dispose();
        }

        GroupManager.getInstance().getLogger().debug("Group with id #" + this.getId() + " was disposed");
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
     * Get the data assigned to a group instance (by the ID)
     *
     * @return The data object
     */
    public GroupData getData() {
        return GroupManager.getInstance().getData(this.id);
    }

    /**
     * Get the membership component
     *
     * @return The component which will handle everything member-related
     */
    public MembershipComponent getMembershipComponent() {
        return membershipComponent;
    }

    /**
     * Get the group forum component
     *
     * @return The group forumc component
     */
    public ForumComponent getForumComponent() {
        return forumComponent;
    }
}
