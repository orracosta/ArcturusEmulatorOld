package com.eu.habbo.habbohotel.rooms;

/**
 * Created on 18-10-2015 14:38.
 */
public enum RoomChatMessageBubbles
{
    NORMAL(0),
    ALERT(1),
    BOT(2),
    RED(3),
    BLUE(4),
    YELLOW(5),
    GREEN(6),
    BLACK(7),
    FORTUNE_TELLER(8),
    ZOMBIE_ARM(9),
    SKELETON(10),
    LIGHT_BLUE(11),
    PINK(12),
    PURPLE(13),
    DARK_YEWLLOW(14),
    DARK_BLUE(15),
    HEARTS(16),
    ROSES(17),
    UNUSED(18), //?
    PIG(19),
    DOG(20),
    BLAZE_IT(21),
    DRAGON(22),
    STAFF(23),
    BATS(24),
    MESSENGER(25),
    STEAMPUNK(26),
    THUNDER(27),
    PARROT(28),
    PIRATE(29),
    BOT_LIGHT_BLUE(30),
    BOT_LIGHT_GRAY(31),
    SCARY_THING(32),
    FRANK(33),
    WIRED(34),
    GOAT(35),
    GRANNY(36);

    private int type;

    RoomChatMessageBubbles(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return this.type;
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
