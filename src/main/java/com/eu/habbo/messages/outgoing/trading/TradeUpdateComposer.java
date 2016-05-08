package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.rooms.RoomTradeUser;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class TradeUpdateComposer extends MessageComposer
{
    private RoomTrade roomTrade;

    public TradeUpdateComposer(RoomTrade roomTrade)
    {
        this.roomTrade = roomTrade;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TradeUpdateComposer);
        for(RoomTradeUser roomTradeUser : this.roomTrade.getRoomTradeUsers())
        {
            this.response.appendInt32(roomTradeUser.getHabbo().getHabboInfo().getId());
            this.response.appendInt32(roomTradeUser.getItems().size());

            for(HabboItem item : roomTradeUser.getItems())
            {
                this.response.appendInt32(item.getId());
                this.response.appendString(item.getBaseItem().getType().toLowerCase());
                this.response.appendInt32(item.getId());
                this.response.appendInt32(item.getBaseItem().getSpriteId());
                this.response.appendInt32(0);
                this.response.appendBoolean(true);
                item.serializeExtradata(this.response);
                this.response.appendInt32(0);
                this.response.appendInt32(0);
                this.response.appendInt32(0);

                if(item.getBaseItem().getType().toLowerCase().equals("s"))
                    this.response.appendInt32(0);
            }
        }
        return this.response;
    }
}
