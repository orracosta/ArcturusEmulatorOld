package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class ClubGiftReceivedComposer extends MessageComposer
{
    //:test 735 s:t i:1 s:s i:230 s:throne i:1 b:1 i:1 i:10;
    private final THashSet<HabboItem> items;

    public ClubGiftReceivedComposer(THashSet<HabboItem> items)
    {
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ClubGiftReceivedComposer);
        this.response.appendInt(this.items.size());

        for(HabboItem item : this.items)
        {
            this.response.appendString(item.getBaseItem().getType().code);
            this.response.appendInt(item.getBaseItem().getId());
            this.response.appendString(item.getBaseItem().getName());
            this.response.appendInt(0);
            this.response.appendBoolean(false);
        }

        return this.response;
    }
}
