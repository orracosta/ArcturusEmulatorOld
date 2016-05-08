package com.eu.habbo.habbohotel.guilds;

/**
 * Created on 23-11-2014 09:31.
 */
public enum GuildRank
{
    ADMIN(0),
    MOD(1),
    MEMBER(2),
    REQUESTED(3),
    DELETED(4);

    public final int type;

    GuildRank(int type)
    {
        this.type = type;
    }

    public static GuildRank getRank(int type)
    {
        for(GuildRank rank : GuildRank.values())
        {
            if(rank.type == type)
                return rank;
        }
        return MEMBER;
    }
}
