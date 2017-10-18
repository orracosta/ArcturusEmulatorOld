package com.eu.habbo.messages.outgoing.handshake;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BannerTokenComposer extends MessageComposer
{

    private final String token;
    private final boolean unknown;

    public BannerTokenComposer(String token, boolean unknown)
    {
        this.token = token;
        this.unknown = unknown;
    }
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BannerTokenComposer);
        this.response.appendString(this.token);
        this.response.appendBoolean(this.unknown);

        return this.response;
    }
}
