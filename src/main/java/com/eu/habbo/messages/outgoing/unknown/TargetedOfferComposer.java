package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.catalog.TargetOffer;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

public class TargetedOfferComposer extends MessageComposer
{
    private final TargetOffer offer;

    public TargetedOfferComposer(TargetOffer offer)
    {
        this.offer = offer;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TargetedOfferComposer);
        this.offer.serialize(this.response);
        return this.response;
    }
}