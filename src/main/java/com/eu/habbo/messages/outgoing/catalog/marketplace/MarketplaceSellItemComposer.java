package com.eu.habbo.messages.outgoing.catalog.marketplace;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class MarketplaceSellItemComposer extends MessageComposer
{
    public static final int NOT_ALLOWED = 2;
    public static final int NO_TRADE_PASS = 3;
    public static final int NO_ADS_LEFT = 4;

    private final int errorCode;
    private final int valueA;
    private final int valueB;

    public MarketplaceSellItemComposer(int errorCode, int valueA, int valueB)
    {
        this.errorCode = errorCode;
        this.valueA = valueA;
        this.valueB = valueB;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MarketplaceSellItemComposer);
        this.response.appendInt32(this.errorCode);
        this.response.appendInt32(this.valueA);
        this.response.appendInt32(this.valueB);
        return this.response;
    }
}
