package com.eu.habbo.habbohotel.rooms;

public enum RoomChatMessageBubbles
{
    NORMAL(0, "", true),
    ALERT(1, "", true),
    BOT(2, "", true),
    RED(3, "", true),
    BLUE(4, "", true),
    YELLOW(5, "", true),
    GREEN(6, "", true),
    BLACK(7, "", true),
    FORTUNE_TELLER(8, "", false),
    ZOMBIE_ARM(9, "", true),
    SKELETON(10, "", true),
    LIGHT_BLUE(11, "", true),
    PINK(12, "", true),
    PURPLE(13, "", true),
    DARK_YEWLLOW(14, "", true),
    DARK_BLUE(15, "", true),
    HEARTS(16, "", true),
    ROSES(17, "", true),
    UNUSED(18, "", true), //?
    PIG(19, "", true),
    DOG(20, "", true),
    BLAZE_IT(21, "", true),
    DRAGON(22, "", true),
    STAFF(23, "", false),
    BATS(24, "", true),
    MESSENGER(25, "", true),
    STEAMPUNK(26, "", true),
    THUNDER(27, "", true),
    PARROT(28, "", false),
    PIRATE(29, "", false),
    BOT_LIGHT_BLUE(30, "", true),
    BOT_LIGHT_GRAY(31, "", true),
    SCARY_THING(32, "", true),
    FRANK(33, "", true),
    WIRED(34, "", false),
    GOAT(35, "", true),
    SANTA(36, "", true),
    AMBASSADOR(37, "acc_ambassador", false),
    UNKNOWN_38(38, "", true),
    UNKNOWN_39(39, "", true),
    UNKNOWN_40(40, "", true),
    UNKNOWN_41(41, "", true),
    UNKNOWN_42(42, "", true),
    UNKNOWN_43(43, "", true),
    UNKNOWN_44(44, "", true),
    UNKNOWN_45(45, "", true);

    private final int type;
    private final String permission;
    private final boolean overridable;

    RoomChatMessageBubbles(int type, String permission, boolean overridable)
    {
        this.type = type;
        this.permission = permission;
        this.overridable = overridable;
    }

    public int getType()
    {
        return this.type;
    }

    public String getPermission()
    {
        return this.permission;
    }

    public boolean isOverridable()
    {
        return this.overridable;
    }

    public static RoomChatMessageBubbles getBubble(int bubbleId)
    {
        try
        {
            return values()[bubbleId];
        }
        catch (Exception e)
        {
            return NORMAL;
        }
    }
}
