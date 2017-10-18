package com.eu.habbo.habbohotel.guilds.forums;

public enum GuildForumState
{
    OPEN(0),
    CLOSED(1),
    HIDDEN_BY_ADMIN(10),
    HIDDEN_BY_STAFF(20);

    private int state;
    public int getState()
    {
        return this.state;
    }

    GuildForumState(int state)
    {
        this.state = state;
    }

    public static GuildForumState fromValue(int value)
    {
        for (GuildForumState state : GuildForumState.values())
        {
            if (state.state == value)
            {
                return state;
            }
        }

        return CLOSED;
    }
}