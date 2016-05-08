package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 11-10-2015 00:13.
 */
public class GuideCloseHelpRequestEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        GuideTour tour = Emulator.getGameEnvironment().getGuideManager().getGuideTourByHabbo(this.client.getHabbo());

        if(tour != null)
        {
            Emulator.getGameEnvironment().getGuideManager().endSession(tour);
        }
    }
}
