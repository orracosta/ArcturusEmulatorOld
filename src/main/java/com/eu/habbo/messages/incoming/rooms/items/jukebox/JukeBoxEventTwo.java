package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxMySongsComposer;

public class JukeBoxEventTwo extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new JukeBoxMySongsComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom().getRoomSpecialTypes().getItemsOfType(InteractionMusicDisc.class)));
    }
}
