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

        this.response.appendInt32(365); //Days Until Next Gift
        this.response.appendInt32(1); //Gift Selectable

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().getCatalogPage(18);
        this.response.appendInt32(page.getCatalogItems().size());

        TIntObjectIterator<CatalogItem> iterator = page.getCatalogItems().iterator();

        for(int i = page.getCatalogItems().size(); i-- > 0;)
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

        this.response.appendInt32(1);
        this.response.appendInt32(-100);
        this.response.appendBoolean(false);
        this.response.appendInt32(-100);
        this.response.appendBoolean(false);

        return this.response;
    }
}
