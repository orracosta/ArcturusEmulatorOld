package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.BannerTokenComposer;

/**
 * Created on 24-8-2014 16:58.
 */
public class RequestBannerToken extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.client.sendResponse(new BannerTokenComposer("Stop loggin, Imma ban your ass", false));
    }
}
