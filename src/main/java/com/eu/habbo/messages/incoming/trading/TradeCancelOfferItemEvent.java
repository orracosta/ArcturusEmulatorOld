package com.eu.habbo.messages.incoming.trading;

import com.eu.habbo.habbohotel.rooms.RoomTrade;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 8-11-2014 14:03.
 */
public class TradeCancelOfferItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        RoomTrade trade = this.client.getHabbo().getHabboInfo().getCurrentRoom().getActiveTradeForHabbo(this.client.getHabbo());
        HabboItem item = this.client.getHabbo().getHabboInventory().getItemsComponent().getHabboItem(itemId);

        if(trade == null || trade.getRoomTradeUserForHabbo(this.client.getHabbo()).getAccepted() || item == null)
        {
            return;
        }

        trade.removeItem(this.client.getHabbo(), item);
    }
}
