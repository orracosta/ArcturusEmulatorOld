package com.eu.habbo.habbohotel.items;

public enum PostItColor
{
    /**
     * Blue!
     */
    BLUE("9CCEFF"),

    /**
     * Green!
     */
    GREEN("9CFF9C"),

    /**
     * Pink! (Or purple :P)
     */
    PINK("FF9CFF"),

    /**
     * Yellow
     */
    YELLOW("FFFF33");

    public final String hexColor;

    PostItColor(String hexColor)
    {
        this.hexColor = hexColor;
    }

    /**
     * If the color is a custom color.
     * @param color The color to check.
     * @return True if the color is a custom color.
     */
    public static boolean isCustomColor(String color)
    {
        for(PostItColor postItColor : PostItColor.values())
        {
            if(postItColor.hexColor.equalsIgnoreCase(color))
                return false;
        }

        return true;
    }
}
