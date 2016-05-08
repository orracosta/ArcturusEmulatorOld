package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MarketplaceConfigComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MarketplaceConfigComposer);

        this.response.appendBoolean(true);
        this.response.appendInt32(1); //Commision Percentage.
        this.response.appendInt32(10); //Credits
        this.response.appendInt32(5); //Advertisements
        this.response.appendInt32(1); //Min price
        this.response.appendInt32(1000000); //Max price
        this.response.appendInt32(48); //Hours in marketplace
        this.response.appendInt32(7); //Days to display

        return this.response;
    }
}
