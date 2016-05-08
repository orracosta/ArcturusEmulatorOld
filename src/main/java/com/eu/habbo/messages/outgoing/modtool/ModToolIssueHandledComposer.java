package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 5-3-2015 09:49.
 */
public class ModToolIssueHandledComposer extends MessageComposer
{
    public static final int HANDLED = 0;
    public static final int USELESS = 1;
    public static final int ABUSIVE = 2;

    private final int code;

    public ModToolIssueHandledComposer(int code)
    {
        this.code = code;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueHandledComposer);
        this.response.appendInt32(this.code);
        this.response.appendString(""); // reportername?
        return this.response;
    }
}
