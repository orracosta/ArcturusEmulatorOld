package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownAvatarEditorComposer extends MessageComposer
{
    private final int type;

    public UnknownAvatarEditorComposer(int type)
    {
        this.type = type;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UnknownAvatarEditorComposer);
        this.response.appendInt(this.type);
        return this.response;
    }
}