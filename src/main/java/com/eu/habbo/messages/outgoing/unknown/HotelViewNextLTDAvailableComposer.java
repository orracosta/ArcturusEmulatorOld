package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class HotelViewNextLTDAvailableComposer extends MessageComposer
{
    private final int unknownInt1;
    private final int pageId;
    private final int unknownInt2;
    private final String unknownString1;

    public HotelViewNextLTDAvailableComposer(int unknownInt1, int pageId, int unknownInt2, String unknownString1)
    {
        this.unknownInt1 = unknownInt1;
        this.pageId = pageId;
        this.unknownInt2 = unknownInt2;
        this.unknownString1 = unknownString1;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.HotelViewNextLTDAvailableComposer);
        this.response.appendInt(this.unknownInt1);
        this.response.appendInt(this.pageId);
        this.response.appendInt(this.unknownInt2);
        this.response.appendString(this.unknownString1);
        return this.response;
    }
}