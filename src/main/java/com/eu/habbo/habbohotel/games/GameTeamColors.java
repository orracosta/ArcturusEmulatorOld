package com.eu.habbo.habbohotel.games;

public enum GameTeamColors
{
    /**
     * Team Red.
     */
    RED(0),

    /**
     * Team Green.
     */
    GREEN(1),

    /**
     * Team Blue.
     */
    BLUE(2),

    /**
     * Team Yellow.
     */
    YELLOW(3),

    /**
     * Additional GameTeamColors
     */
    NONE(4),

    ONE(5),
    TWO(6),
    THREE(7),
    FOUR(8),
    FIVE(9),
    SIX(10),
    SEVEN(11),
    EIGHT(12),
    NINE(13),
    TEN(14);

    /**
     * The type of the TeamColor. Aka identifier.
     */
    public final int type;

    GameTeamColors(int type)
    {
        this.type = type;
    }

    public static GameTeamColors fromType(int type)
    {
        for (GameTeamColors teamColors : values())
        {
            if (teamColors.type == type)
            {
                return teamColors;
            }
        }

        return RED;
    }
}
