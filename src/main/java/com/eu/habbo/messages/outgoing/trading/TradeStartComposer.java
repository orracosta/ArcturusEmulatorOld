package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class TradeStartComposer extends MessageComposer
{
    private RoomTrade roomTrade;

    public TradeStartComposer(RoomTrade roomTrade)
    {
        this.roomTrade = roomTrade;
    }
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TradeStartComposer);
        for(RoomTradeUser tradeUser : this.roomTrade.getRoomTradeUsers())
        {
            this.response.appendInt(tradeUser.getHabbo().getHabboInfo().getId());
            this.response.appendInt(1);
        }
        return this.response;
    }
}
