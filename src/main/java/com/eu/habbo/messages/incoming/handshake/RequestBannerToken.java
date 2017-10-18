package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.BannerTokenComposer;

public class RequestBannerToken extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new BannerTokenComposer("Stop loggin, Imma ban your ass", false));
    }
}
