package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorSettingsComposer;

/**
 * Created on 20-11-2015 20:33.
 */
public class RequestNavigatorSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new NewNavigatorSettingsComposer(this.client.getHabbo().getHabboStats().navigatorWindowSettings));
    }
}
