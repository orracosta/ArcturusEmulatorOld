package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-11-2015 20:42.
 */
public class BullyReportedMessageComposer extends MessageComposer
{
    public static final int RECEIVED = 0;
    public static final int IGNORED = 1;
    public static final int NO_CHAT = 2;
    public static final int ALREADY_REPORTED = 3;
    public static final int NO_MISSUSE = 4;

    private final int code;

    public BullyReportedMessageComposer(int code)
    {
        this.code = code;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BullyReportedMessageComposer);
        this.response.appendInt32(this.code);
        return this.response;
    }
}
