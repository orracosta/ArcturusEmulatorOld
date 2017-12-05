package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Map;

public class GiftConfigurationComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GiftConfigurationComposer);

        this.response.appendBoolean(true);
        this.response.appendInt(Emulator.getConfig().getInt("hotel.gifts.special.price", 2));

        this.response.appendInt(Emulator.getGameEnvironment().getCatalogManager().giftWrappers.size());
        for(Integer i : Emulator.getGameEnvironment().getCatalogManager().giftWrappers.keySet())
        {
            this.response.appendInt(i);
        }

        this.response.appendInt(8);
        this.response.appendInt(0);
        this.response.appendInt(1);
        this.response.appendInt(2);
        this.response.appendInt(3);
        this.response.appendInt(4);
        this.response.appendInt(5);
        this.response.appendInt(6);
        this.response.appendInt(8);

        this.response.appendInt(11);
        this.response.appendInt(0);
        this.response.appendInt(1);
        this.response.appendInt(2);
        this.response.appendInt(3);
        this.response.appendInt(4);
        this.response.appendInt(5);
        this.response.appendInt(6);
        this.response.appendInt(7);
        this.response.appendInt(8);
        this.response.appendInt(9);
        this.response.appendInt(10);

        this.response.appendInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size());

        for(Map.Entry<Integer, Integer> set : Emulator.getGameEnvironment().getCatalogManager().giftFurnis.entrySet())
        {
            this.response.appendInt(set.getKey());
        }

        return this.response;
    }
}
