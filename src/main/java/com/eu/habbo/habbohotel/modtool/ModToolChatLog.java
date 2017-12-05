package com.eu.habbo.habbohotel.modtool;

public class ModToolChatLog implements Comparable<ModToolChatLog>
{
    public final int timestamp;
    public final int habboId;
    public final String username;
    public final String message;

    public ModToolChatLog(int timestamp, int habboId, String username, String message)
    {
        this.timestamp = timestamp;
        this.habboId = habboId;
        this.username = username;
        this.message = message;
    }

    @Override
    public int compareTo(ModToolChatLog o)
    {
        return o.timestamp - this.timestamp;
    }
}
