package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

/**
 * Created on 27-8-2014 15:58.
 */
public class CatalogPagesListComposer extends MessageComposer {

    private final Habbo habbo;

    public CatalogPagesListComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        try
        {
            List<CatalogPage> pages = Emulator.getGameEnvironment().getCatalogManager().getCatalogPages(-1, this.habbo);

            this.response.init(Outgoing.CatalogPagesListComposer);

            this.response.appendBoolean(true);
            this.response.appendInt32(0);
            this.response.appendInt32(-1);
            this.response.appendString("root");
            this.response.appendString("");
            this.response.appendInt32(0);
            this.response.appendInt32(pages.size());

            boolean hasPermission = this.habbo.hasPermission("acc_catalog_ids");

            for (CatalogPage category : pages)
            {
                List<CatalogPage> pagesList = Emulator.getGameEnvironment().getCatalogManager().getCatalogPages(category.getId(), this.habbo);

                this.response.appendBoolean(category.isVisible());
                this.response.appendInt32(category.getIconImage());
                this.response.appendInt32(category.getId());
                this.response.appendString(category.getPageName());
                this.response.appendString(category.getCaption() + (hasPermission ? " (" + category.getId() + ")" : ""));
                this.response.appendInt32(category.getOfferIds().size());

                for(int i : category.getOfferIds().toArray())
                {
                    this.response.appendInt32(i);
                }

                this.response.appendInt32(pagesList.size());

                for (CatalogPage page : pagesList)
                {
                    this.response.appendBoolean(page.isVisible());
                    this.response.appendInt32(page.getIconImage());
                    this.response.appendInt32(page.getId());
                    this.response.appendString(page.getPageName());
                    this.response.appendString(page.getCaption() + (hasPermission ? " (" + page.getId() + ")" : ""));
                    this.response.appendInt32(page.getOfferIds().size());

                    for(int i : page.getOfferIds().toArray())
                    {
                        this.response.appendInt32(i);
                    }

                    this.response.appendInt32(0);
                }
            }

            this.response.appendBoolean(false);
            this.response.appendString("NORMAL");

            return this.response;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
