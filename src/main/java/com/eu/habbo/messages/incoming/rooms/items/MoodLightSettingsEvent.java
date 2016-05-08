package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.MoodLightDataComposer;

/**
 * Created on 14-11-2014 20:30.
 */
public class MoodLightSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
            this.client.sendResponse(new MoodLightDataComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom().getMoodlightData()));
    }
}
