package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.List;

public class JukeBoxMySongsComposer extends MessageComposer
{
    private final List<InteractionMusicDisc> items;

    public JukeBoxMySongsComposer(List<InteractionMusicDisc> items)
    {
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxMySongsComposer);

        this.response.appendInt(this.items.size());

        for(HabboItem item : items)
        {
            this.response.appendInt(item.getId());
            this.response.appendInt(((InteractionMusicDisc) item).getSongId());
        }

        return this.response;
    }
}
