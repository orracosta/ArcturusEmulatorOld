package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class ItemsComponent
{
    private final TIntObjectMap<HabboItem> items = TCollections.synchronizedMap(new TIntObjectHashMap<HabboItem>());

    public ItemsComponent(Habbo habbo)
    {
        this.items.putAll(loadItems(habbo));
    }

    public static THashMap<Integer, HabboItem> loadItems(Habbo habbo)
    {
        THashMap<Integer, HabboItem> itemsList = new THashMap<Integer, HabboItem>();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM items WHERE room_id = ? AND user_id = ?");
            statement.setInt(1, 0);
            statement.setInt(2, habbo.getHabboInfo().getId());
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);

                if(item != null)
                {
                    itemsList.put(set.getInt("id"), item);
                }
                else
                {
                    Emulator.getLogging().logErrorLine("Failed to load HabboItem: " + set.getInt("id"));
                }
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return itemsList;
    }

    public void addItem(HabboItem item)
    {
        if(item == null)
        {
            throw new NullPointerException("Tried adding NULL HabboItem to inventory!");
        }

        synchronized (this.items)
        {
            this.items.put(item.getId(), item);
        }
    }

    public void addItems(THashSet<HabboItem> items)
    {
        synchronized (this.items)
        {
            for (HabboItem item : items)
            {
                if(item == null)
                {
                    throw new NullPointerException("Tried adding NULL HabboItem to inventory!");
                }

                this.items.put(item.getId(), item);
            }
        }
    }

    public HabboItem getHabboItem(int itemId)
    {
        return this.items.get(itemId);
    }

    public void removeHabboItem(int itemId)
    {
        this.items.remove(itemId);
    }

    public void removeHabboItem(HabboItem item)
    {
        synchronized (this.items)
        {
            this.items.remove(item.getId());
        }
    }

    public TIntObjectMap<HabboItem> getItems()
    {
        return this.items;
    }

    public THashSet<HabboItem> getItemsAsValueCollection()
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        items.addAll(this.items.valueCollection());

        return items;
    }

    public void dispose()
    {
        synchronized (this.items)
        {
            TIntObjectIterator<HabboItem> items = this.items.iterator();

            if (items == null)
            {
                Emulator.getLogging().logErrorLine(new NullPointerException("Items is NULL!"));
                return;
            }

            if (!this.items.isEmpty())
            {
                for (int i = this.items.size(); i-- > 0; )
                {
                    try
                    {
                        items.advance();
                    } catch (NoSuchElementException e)
                    {
                        break;
                    }
                    if (items.value().needsUpdate())
                        Emulator.getThreading().run(items.value());
                }
            }

            this.items.clear();
        }
    }
}
