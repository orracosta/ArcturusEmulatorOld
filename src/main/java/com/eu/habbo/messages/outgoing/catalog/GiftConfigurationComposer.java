package com.eu.habbo.messages.outgoing.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.Map;

/**
 * Created on 27-8-2014 15:19.
 */
public class GiftConfigurationComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.GiftConfigurationComposer);

        this.response.appendBoolean(true);
        this.response.appendInt32(Emulator.getConfig().getInt("hotel.gifts.special.price", 2));

        this.response.appendInt32(Emulator.getGameEnvironment().getCatalogManager().giftWrappers.size());
        for(Integer i : Emulator.getGameEnvironment().getCatalogManager().giftWrappers.keySet())
        {
            this.response.appendInt32(i);
        }

        this.response.appendInt32(8);
        this.response.appendInt32(0);
        this.response.appendInt32(1);
        this.response.appendInt32(2);
        this.response.appendInt32(3);
        this.response.appendInt32(4);
        this.response.appendInt32(5);
        this.response.appendInt32(6);
        this.response.appendInt32(8);

        this.response.appendInt32(11);
        this.response.appendInt32(0);
        this.response.appendInt32(1);
        this.response.appendInt32(2);
        this.response.appendInt32(3);
        this.response.appendInt32(4);
        this.response.appendInt32(5);
        this.response.appendInt32(6);
        this.response.appendInt32(7);
        this.response.appendInt32(8);
        this.response.appendInt32(9);
        this.response.appendInt32(10);

        this.response.appendInt32(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size());

        for(Map.Entry<Integer, Integer> set : Emulator.getGameEnvironment().getCatalogManager().giftFurnis.entrySet())
        {
            this.response.appendInt32(set.getKey());
        }

        return this.response;
    }
}
