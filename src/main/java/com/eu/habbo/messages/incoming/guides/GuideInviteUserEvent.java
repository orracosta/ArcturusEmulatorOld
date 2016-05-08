package com.eu.habbo.messages.incoming.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guides.GuideTour;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guides.GuideSessionInvitedToGuideRoomComposer;

/**
 * Created on 10-10-2015 23:33.
 */
public class GuideInviteUserEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        GuideTour tour = Emulator.getGameEnvironment().getGuideManager().getGuideTourByHelper(this.client.getHabbo());

        if(tour != null)
        {
            ServerMessage message = new GuideSessionInvitedToGuideRoomComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom()).compose();
            tour.getNoob().getClient().sendResponse(message);
            tour.getHelper().getClient().sendResponse(message);
        }
    }
}
