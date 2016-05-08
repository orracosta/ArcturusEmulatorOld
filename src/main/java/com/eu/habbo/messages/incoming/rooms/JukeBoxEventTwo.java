package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.JukeBoxMySongsComposer;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 5-9-2015 12:30.
 */
public class JukeBoxEventTwo extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new JukeBoxMySongsComposer(this.client.getHabbo().getHabboInfo().getCurrentRoom().getRoomSpecialTypes().getItemsOfType(InteractionMusicDisc.class)));
    }
}
