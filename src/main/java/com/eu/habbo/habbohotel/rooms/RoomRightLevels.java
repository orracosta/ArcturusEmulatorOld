package com.eu.habbo.habbohotel.rooms;

public enum RoomRightLevels
{
    /**
     * No Rights.
     */
    NONE        (0),

    /**
     * Rights given by the room owner.
     */
    RIGHTS      (1),

    /**
     * Rights trough guild allowing members to build.
     */
    GUILD_RIGHTS(2),

    /**
     * Rights as Admin of the guild.
     */
    GUILD_ADMIN (3),

    /**
     * Room owner rights.
     */
    OWNER       (4),

    /**
     * Moderator rights.
     */
    MODERATOR   (5),

    /**
     * Six. Unknown.
     */
    SIX         (6),

    /**
     * Seven. Unknown.
     */
    SEVEN       (7),

    /**
     * Eight. Unknown.
     */
    EIGHT       (8),

    /**
     * Nine. Unknown.
     */
    NINE        (9);

    public final int level;

    RoomRightLevels(int level)
    {
        this.level = level;
    }
}
