package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 29-8-2014 15:46.
 */
public class AddHabboItemComposer extends MessageComposer {

    private THashSet<HabboItem> itemsList;
    private HabboItem item;

    public AddHabboItemComposer(THashSet<HabboItem> itemsList)
    {
        this.itemsList = itemsList;
    }

    public AddHabboItemComposer(HabboItem item)
    {
        this.item = item;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.AddHabboItemComposer);

        if(item == null) {
            this.response.appendInt32(1);
            this.response.appendInt32(1);
            this.response.appendInt32(this.itemsList.size());
            for (HabboItem habboItem : this.itemsList) {
                this.response.appendInt32(habboItem.getId());
            }
        }
        else
        {
            this.response.appendInt32(1);
            this.response.appendInt32(1);
            this.response.appendInt32(1);
            this.response.appendInt32(this.item.getId());
        }
        return this.response;
    }
}
