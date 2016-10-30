package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxNowPlayingMessageComposer;

public class JukeBoxEventOne extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new JukeBoxNowPlayingMessageComposer(null, -1, -1));
    }
}
