package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ModToolIssueHandledComposer extends MessageComposer
{
    public static final int HANDLED = 0;
    public static final int USELESS = 1;
    public static final int ABUSIVE = 2;

    private int code = 0;
    private String message = "";

    public ModToolIssueHandledComposer(int code)
    {
        this.code = code;
    }

    public ModToolIssueHandledComposer(String message)
    {
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueHandledComposer);
        this.response.appendInt(this.code);
        this.response.appendString(this.message);
        return this.response;
    }
}
