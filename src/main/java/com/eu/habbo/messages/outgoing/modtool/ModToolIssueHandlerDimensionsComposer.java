package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class ModToolIssueHandlerDimensionsComposer extends MessageComposer
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public ModToolIssueHandlerDimensionsComposer(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueHandlerDimensionsComposer);
        this.response.appendInt32(this.x);
        this.response.appendInt32(this.y);
        this.response.appendInt32(this.width);
        this.response.appendInt32(this.height);
        return this.response;
    }
}