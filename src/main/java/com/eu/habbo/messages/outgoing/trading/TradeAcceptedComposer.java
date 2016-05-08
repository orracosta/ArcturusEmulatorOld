package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 8-11-2014 13:47.
 */
public class TradeAcceptedComposer extends MessageComposer
{
    private final RoomTradeUser tradeUser;

    public TradeAcceptedComposer(RoomTradeUser tradeUser)
    {
        this.tradeUser = tradeUser;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TradeAcceptedComposer);
        this.response.appendInt32(this.tradeUser.getHabbo().getHabboInfo().getId());
        this.response.appendInt32(this.tradeUser.getAccepted());
        return this.response;
    }
}
