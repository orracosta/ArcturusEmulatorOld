package com.eu.habbo.habbohotel.rooms;

public enum RoomChatMessageBubbles
{
    NORMAL(0, ""),
    ALERT(1, ""),
    BOT(2, ""),
    RED(3, ""),
    BLUE(4, ""),
    YELLOW(5, ""),
    GREEN(6, ""),
    BLACK(7, ""),
    FORTUNE_TELLER(8, ""),
    ZOMBIE_ARM(9, ""),
    SKELETON(10, ""),
    LIGHT_BLUE(11, ""),
    PINK(12, ""),
    PURPLE(13, ""),
    DARK_YEWLLOW(14, ""),
    DARK_BLUE(15, ""),
    HEARTS(16, ""),
    ROSES(17, ""),
    UNUSED(18, ""), //?
    PIG(19, ""),
    DOG(20, ""),
    BLAZE_IT(21, ""),
    DRAGON(22, ""),
    STAFF(23, ""),
    BATS(24, ""),
    MESSENGER(25, ""),
    STEAMPUNK(26, ""),
    THUNDER(27, ""),
    PARROT(28, ""),
    PIRATE(29, ""),
    BOT_LIGHT_BLUE(30, ""),
    BOT_LIGHT_GRAY(31, ""),
    SCARY_THING(32, ""),
    FRANK(33, ""),
    WIRED(34, ""),
    GOAT(35, ""),
    SANTA(36, ""),
    AMBASSADOR(37, "acc_ambassador"),
    UNKNOWN_38(38, ""),
    UNKNOWN_39(39, ""),
    UNKNOWN_40(40, ""),
    UNKNOWN_41(41, ""),
    UNKNOWN_42(42, ""),
    UNKNOWN_43(43, ""),
    UNKNOWN_44(44, ""),
    UNKNOWN_45(45, "");

    private final int type;
    private final String permission;

    RoomChatMessageBubbles(int type, String permission)
    {
        this.type = type;
        this.permission = permission;
    }

    public int getType()
    {
        return this.type;
    }

    public String getPermission()
    {
        return this.permission;
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
