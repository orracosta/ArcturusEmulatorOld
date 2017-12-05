package com.eu.habbo.plugin.events.support;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

public class SupportRoomActionEvent extends SupportEvent
{
    /**
     * The room to handle.
     */
    public final Room room;

    /**
     * Whether to kick all users.
     */
    public boolean kickUsers;

    /**
     * Whether to lock the door.
     */
    public boolean lockDoor;

    /**
     * Whether to change the title.
     */
    public boolean changeTitle;

    /**
     * This event is triggered when the room settings are changed via the mod tool.
     * @param moderator The moderator for this event.
     * @param room The room to handle.
     * @param kickUsers Whether to kick all users.
     * @param lockDoor Whether to lock the door.
     * @param changeTitle Whether to change the title.
     */
    public SupportRoomActionEvent(Habbo moderator, Room room, boolean kickUsers, boolean lockDoor, boolean changeTitle)
    {
        super(moderator);

        this.room        = room;
        this.kickUsers   = kickUsers;
        this.lockDoor    = lockDoor;
        this.changeTitle = changeTitle;
    }
}