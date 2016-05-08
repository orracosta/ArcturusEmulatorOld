package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 17-11-2015 20:34.
 */
public class BullyReportClosedComposer extends MessageComposer
{
    public final static int CLOSED = 1;
    public final static int MISUSE = 2;

    public final int code;

    public BullyReportClosedComposer(int code)
    {
        this.code = code;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BullyReportClosedComposer);
        this.response.appendInt32(this.code);
        return this.response;
    }
}
