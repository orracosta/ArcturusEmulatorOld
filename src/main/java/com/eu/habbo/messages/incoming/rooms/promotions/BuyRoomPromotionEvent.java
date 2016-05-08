package com.eu.habbo.messages.incoming.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.AlertPurchaseFailedComposer;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;

/**
 * Created on 15-11-2015 14:37.
 */
public class BuyRoomPromotionEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        //TODO Copy from Azure.
        int pageId = this.packet.readInt();

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().getCatalogPage(pageId);

        if(page != null)
        {
            CatalogItem item = page.getCatalogItem(this.packet.readInt());
            if(item != null)
            {
                if(this.client.getHabbo().getHabboInfo().canBuy(item))
                {
                    Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.packet.readInt());

                    if (room.isPromoted())
                    {
                        room.getPromotion().addEndTimestamp(120 * 60);
                    } else
                    {
                        room.createPromotion(this.packet.readString(), this.packet.readString());
                    }

                    if(room.isPromoted())
                    {
                        this.client.getHabbo().giveCredits(-item.getCredits());
                        this.client.getHabbo().givePoints(item.getPointsType(), -item.getPoints());
                        this.client.sendResponse(new PurchaseOKComposer());
                    }
                    else
                    {
                        this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
                    }
                }
            }
        }
    }
}
