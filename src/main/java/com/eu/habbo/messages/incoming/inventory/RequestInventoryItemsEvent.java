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
        int totalItems = this.client.getHabbo().getInventory().getItemsComponent().getItems().size();
        int pages = (int) Math.ceil((double) totalItems / 1000.0);

        if (pages == 0)
        {
            pages = 1;
        }

        synchronized (this.client.getHabbo().getInventory().getItemsComponent().getItems())
        {
            TIntObjectMap<HabboItem> items = new TIntObjectHashMap<HabboItem>();
            TIntObjectIterator<HabboItem> iterator = this.client.getHabbo().getInventory().getItemsComponent().getItems().iterator();
            int count = 0;
            int page = 0;
            for (int i = this.client.getHabbo().getInventory().getItemsComponent().getItems().size(); i-- > 0; )
            {
                if (count == 0)
                {
                    page++;
                }

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
                    count = 0;
                    items.clear();
                }
            }

            this.client.sendResponse(new InventoryItemsComposer(page, pages, items));
        }
    }
}
