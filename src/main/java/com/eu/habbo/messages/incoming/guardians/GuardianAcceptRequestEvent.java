package com.eu.habbo.messages.incoming.guardians;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 17-10-2015 13:29.
 */
public class GuardianAcceptRequestEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Emulator.getGameEnvironment().getGuideManager().acceptTicket(this.client.getHabbo(), this.packet.readBoolean());
    }
}
