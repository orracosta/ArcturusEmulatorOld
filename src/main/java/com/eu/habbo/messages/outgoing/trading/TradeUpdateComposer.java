package com.eu.habbo.messages.outgoing.trading;

import com.eu.habbo.habbohotel.items.FurnitureType;
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
            this.response.appendInt(roomTradeUser.getHabbo().getHabboInfo().getId());
            this.response.appendInt(roomTradeUser.getItems().size());

            for(HabboItem item : roomTradeUser.getItems())
            {
                this.response.appendInt(item.getId());
                this.response.appendString(item.getBaseItem().getType().code);
                this.response.appendInt(item.getId());
                this.response.appendInt(item.getBaseItem().getSpriteId());
                this.response.appendInt(0);
                this.response.appendBoolean(item.getBaseItem().allowInventoryStack() && !item.isLimited());
                item.serializeExtradata(this.response);
                this.response.appendInt(0);
                this.response.appendInt(0);
                this.response.appendInt(0);

                if(item.getBaseItem().getType() == FurnitureType.FLOOR)
                    this.response.appendInt(0);
            }

            this.response.appendInt(0);
            this.response.appendInt(0);
        }
        return this.response;
    }
}
