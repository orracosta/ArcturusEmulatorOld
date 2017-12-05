package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.iterator.TIntObjectIterator;

import java.util.NoSuchElementException;

public class ClubGiftsComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ClubGiftsComposer);

        this.response.appendInt(365); //Days Until Next Gift
        this.response.appendInt(1); //Gift Selectable

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().getCatalogPage(Emulator.getConfig().getInt("catalog.page.vipgifts"));

        if (page != null)
        {
            this.response.appendInt(page.getCatalogItems().size());

            TIntObjectIterator<CatalogItem> iterator = page.getCatalogItems().iterator();
            for (int i = page.getCatalogItems().size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    CatalogItem item = iterator.value();

                    if (item != null)
                    {
                        item.serialize(this.response);
                    }
                }
                catch (NoSuchElementException e)
                {
                    break;
                }
            }

            this.response.appendInt(page.getCatalogItems().size());
            iterator = page.getCatalogItems().iterator();
            for (int i = page.getCatalogItems().size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    CatalogItem item = iterator.value();

                    if (item != null)
                    {
                        this.response.appendInt(1);
                        this.response.appendBoolean(true);
                        this.response.appendInt(2);
                        this.response.appendBoolean(true);
                    }
                    else
                    {
                        this.response.appendInt(-100);
                        this.response.appendBoolean(false);
                        this.response.appendInt(-100);
                        this.response.appendBoolean(false);
                    }
                }
                catch (NoSuchElementException e)
                {
                    break;
                }
            }
        }
        else
        {
            this.response.appendInt(0);
            this.response.appendInt(0);
        }

        return this.response;
    }
}
