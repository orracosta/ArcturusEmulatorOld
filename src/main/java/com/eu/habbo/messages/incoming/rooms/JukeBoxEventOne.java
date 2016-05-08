package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.JukeBoxNowPlayingMessageComposer;

public class JukeBoxEventOne extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new JukeBoxNowPlayingMessageComposer(null, -1, -1));
    }
}
