package com.eu.habbo.habbohotel.modtool;

public enum CfhActionType
{
    MODS(0),
    AUTO_REPLY(1),
    AUTO_IGNORE(2);

    public final int type;

    CfhActionType(int type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        switch(this)
        {
            case AUTO_REPLY:
                return "auto_reply";

            case AUTO_IGNORE:
                return "auto_ignore";
        }

        return "mods";
    }

    public static CfhActionType get(String name)
    {
        switch(name)
        {
            case "auto_reply":
                return CfhActionType.AUTO_REPLY;

            case "auto_ignore":
                return CfhActionType.AUTO_IGNORE;
        }

        return CfhActionType.MODS;
    }
}