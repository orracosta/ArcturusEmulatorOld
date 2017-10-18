package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HotelViewHideCommunityVoteButtonComposer extends MessageComposer
{
    private final boolean unknownBoolean;

    public HotelViewHideCommunityVoteButtonComposer(boolean unknownBoolean)
    {
        this.unknownBoolean = unknownBoolean;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HotelViewHideCommunityVoteButtonComposer);
        this.response.appendBoolean(this.unknownBoolean);
        return this.response;
    }
}