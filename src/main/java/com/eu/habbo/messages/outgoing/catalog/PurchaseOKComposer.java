package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 29-8-2014 15:54.
 */
public class PurchaseOKComposer extends MessageComposer {

    private final CatalogItem catalogItem;
    private final Item item;

    public PurchaseOKComposer(CatalogItem catalogItem, Item item)
    {
        this.catalogItem = catalogItem;
        this.item = item;
    }

    public PurchaseOKComposer()
    {
        this.catalogItem = null;
        this.item = null;
    }

    @Override
    public ServerMessage compose() {

        this.response.init(Outgoing.PurchaseOKComposer);
        if(catalogItem != null && item != null)
        {
            this.response.appendInt32(this.item.getId());
            this.response.appendString(this.item.getName());
            this.response.appendBoolean(false);
            this.response.appendInt32(this.catalogItem.getCredits());
            this.response.appendInt32(this.catalogItem.getPoints());
            this.response.appendInt32(this.catalogItem.getPointsType());
            this.response.appendBoolean(false);
            this.response.appendInt32(1);
            this.response.appendString(this.item.getType().toLowerCase());
            this.response.appendInt32(this.item.getSpriteId());
            this.response.appendString("");
            this.response.appendInt32(1);
            this.response.appendInt32(false);
            this.response.appendInt32(0);
            this.response.appendBoolean(true);
        }
        else if(catalogItem != null)
        {
            this.response.appendInt32(0);
            this.response.appendString(this.catalogItem.getName());
            this.response.appendBoolean(false);
            this.response.appendInt32(this.catalogItem.getCredits());
            this.response.appendInt32(this.catalogItem.getPoints());
            this.response.appendInt32(this.catalogItem.getPointsType());
            this.response.appendBoolean(false);
            this.response.appendInt32(1);
            this.response.appendString("s");
            this.response.appendInt32(0);
            this.response.appendString("");
            this.response.appendInt32(1);
            this.response.appendInt32(false);
            this.response.appendInt32(0);
            this.response.appendBoolean(true);
        }
        else
        {
            this.response.appendInt32(0);
            this.response.appendString("");
            this.response.appendBoolean(false);
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            this.response.appendBoolean(true);
            this.response.appendInt32(1);
            this.response.appendString("s");
            this.response.appendInt32(0);
            this.response.appendString("");
            this.response.appendInt32(1);
            this.response.appendInt32(0);
            this.response.appendString("");
            this.response.appendInt32(1);
        }
        return this.response;
    }
}
