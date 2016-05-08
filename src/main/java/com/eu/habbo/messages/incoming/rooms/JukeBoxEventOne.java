package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.JukeBoxNowPlayingMessageComposer;

/**
 * Created on 5-9-2015 12:30.
 */
public class JukeBoxEventOne extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new JukeBoxNowPlayingMessageComposer(null, -1, -1));
    }
}
