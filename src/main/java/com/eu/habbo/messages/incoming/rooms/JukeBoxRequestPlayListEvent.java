package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.JukeBoxPlayListComposer;
import gnu.trove.set.hash.THashSet;

public class JukeBoxRequestPlayListEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new JukeBoxPlayListComposer(new THashSet<SoundTrack>()));
    }
}
