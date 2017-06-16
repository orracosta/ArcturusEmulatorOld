package com.eu.habbo.habbohotel.items;

public enum FurnitureType
{
    FLOOR("s"),
    WALL("i"),
    EFFECT("e"),
    BADGE("b"),
    ROBOT("r"),
    HABBO_CLUB("h"),
    PET("p");

    public final String code;

    FurnitureType(String code)
    {
        this.code = code;
    }

    public static FurnitureType fromString(String code)
    {
        switch (code.toLowerCase())
        {
            case "s":
                return FLOOR;
            case "i":
                return WALL;
            case "e":
                return EFFECT;
            case "b":
                return BADGE;
            case "r":
                return ROBOT;
            case "h":
                return HABBO_CLUB;
            case "p":
                return PET;
            default:
                return FLOOR;
        }

    }
}