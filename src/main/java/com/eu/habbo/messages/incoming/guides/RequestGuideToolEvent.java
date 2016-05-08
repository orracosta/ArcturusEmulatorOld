package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guides.GuideToolsComposer;

/**
 * Created on 11-10-2015 00:33.
 */
public class RequestGuideToolEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        boolean onDuty = this.packet.readBoolean();

        if(onDuty)
        {
            boolean tourRequests = this.packet.readBoolean();
            boolean helperRequests = this.packet.readBoolean();
            boolean bullyReports = this.packet.readBoolean();

            if (helperRequests)
            {
                Emulator.getGameEnvironment().getGuideManager().setOnGuide(this.client.getHabbo(), onDuty);
            }

            if(bullyReports)
            {
                Emulator.getGameEnvironment().getGuideManager().setOnGuardian(this.client.getHabbo(), onDuty);
            }

            this.client.sendResponse(new GuideToolsComposer(onDuty));
        }
        else
        {
            Emulator.getGameEnvironment().getGuideManager().setOnGuide(this.client.getHabbo(), onDuty);
            Emulator.getGameEnvironment().getGuideManager().setOnGuardian(this.client.getHabbo(), onDuty);
            this.client.sendResponse(new GuideToolsComposer(onDuty));
        }
    }
}
