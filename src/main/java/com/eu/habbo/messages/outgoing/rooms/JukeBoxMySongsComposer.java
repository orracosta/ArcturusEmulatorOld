package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class JukeBoxMySongsComposer extends MessageComposer
{
    private final THashSet<HabboItem> items;

    public JukeBoxMySongsComposer(THashSet<HabboItem> items)
    {
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxMySongsComposer);

        this.response.appendInt32(this.items.size());

        for(HabboItem item : items)
        {
            this.response.appendInt32(item.getId());
            this.response.appendInt32(((InteractionMusicDisc)item).getSongId());
        }

        return this.response;
    }
}
