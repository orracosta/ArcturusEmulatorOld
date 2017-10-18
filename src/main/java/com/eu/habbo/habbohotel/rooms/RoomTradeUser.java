package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;

public class RoomTradeUser
{
    private final Habbo habbo;
    private boolean accepted;
    private boolean confirmed;
    private final THashSet<HabboItem> items;

    public RoomTradeUser(Habbo habbo)
    {
        this.habbo = habbo;
        this.accepted = false;
        this.confirmed = false;
        this.items = new THashSet<HabboItem>();
    }

    public Habbo getHabbo()
    {
        return this.habbo;
    }

    public boolean getAccepted()
    {
        return this.accepted;
    }

    public boolean getConfirmed()
    {
        return this.confirmed;
    }

    public void setAccepted(boolean value)
    {
        this.accepted = value;
    }

    public void confirm()
    {
        this.confirmed = true;
    }

    public void addItem(HabboItem item)
    {
        this.items.add(item);
    }

    public THashSet<HabboItem> getItems()
    {
        return this.items;
    }

    public void clearItems()
    {
        this.items.clear();
    }
}

