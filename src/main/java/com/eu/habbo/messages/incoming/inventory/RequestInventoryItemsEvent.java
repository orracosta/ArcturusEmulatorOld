package com.eu.habbo.messages.incoming.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.inventory.InventoryItemsComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.NoSuchElementException;

public class RequestInventoryItemsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int totalItems = this.client.getHabbo().getHabboInventory().getItemsComponent().getItems().size();
        int pages = (int)Math.ceil((double)totalItems / 1000.0);

        synchronized (this.client.getHabbo().getHabboInventory().getItemsComponent().getItems())
        {
            TIntObjectMap<HabboItem> items = new TIntObjectHashMap<>();
            TIntObjectIterator<HabboItem> iterator = this.client.getHabbo().getHabboInventory().getItemsComponent().getItems().iterator();
            int count = 0;
            int page = 1;
            for (int i = this.client.getHabbo().getHabboInventory().getItemsComponent().getItems().size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();
                    items.put(iterator.key(), iterator.value());
                    count++;
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }

                if (count == 1000)
                {
                    this.client.sendResponse(new InventoryItemsComposer(page, pages, items));
                    page++;
                    count = 0;
                    items.clear();
                }
            }

            if (!items.isEmpty())
            {
                this.client.sendResponse(new InventoryItemsComposer(page, pages, items));
            }
        }
    }
}
