package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 2-8-2015 15:48.
 */
public class GuildEditFailComposer extends MessageComposer
{
    public static final int ROOM_ALREADY_IN_USE = 0;
    public static final int INVALID_GUILD_NAME = 1;
    public static final int HC_REQUIRED = 2;
    public static final int MAX_GUILDS_JOINED = 3;

    private int errorCode;

    public GuildEditFailComposer(int errorCode)
    {
        this.errorCode = errorCode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildEditFailComposer);
        this.response.appendInt32(this.errorCode);
        return this.response;
    }
}
