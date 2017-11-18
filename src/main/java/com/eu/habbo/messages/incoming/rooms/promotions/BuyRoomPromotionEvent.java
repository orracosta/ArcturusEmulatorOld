package com.eu.habbo.messages.incoming.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.AlertPurchaseFailedComposer;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKComposer;
import com.eu.habbo.messages.outgoing.rooms.promotions.RoomPromotionMessageComposer;

public class BuyRoomPromotionEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int pageId = this.packet.readInt();
        int itemId = this.packet.readInt();
        int roomId = this.packet.readInt();
        String title = this.packet.readString();
        boolean unknown1 = this.packet.readBoolean();
        String description = this.packet.readString();
        int categoryId = this.packet.readInt();

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().getCatalogPage(pageId);

        if(page != null)
        {
            CatalogItem item = page.getCatalogItem(itemId);
            if(item != null)
            {
                if(this.client.getHabbo().getHabboInfo().canBuy(item))
                {
                    Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

                    if (!(room.isOwner(this.client.getHabbo()) || room.hasRights(this.client.getHabbo()) || room.guildRightLevel(this.client.getHabbo()) == 3))
                    {
                        return;
                    }

                    if (room.isPromoted())
                    {
                        room.getPromotion().addEndTimestamp(120 * 60);
                    } else
                    {
                        room.createPromotion(title, description);
                    }

                    if(room.isPromoted())
                    {
                        if (!this.client.getHabbo().hasPermission("acc_infinite_credits"))
                            this.client.getHabbo().giveCredits(-item.getCredits());
                        if (!this.client.getHabbo().hasPermission("acc_infinite_points"))
                            this.client.getHabbo().givePoints(item.getPointsType(), -item.getPoints());
                        this.client.sendResponse(new PurchaseOKComposer());
                        room.sendComposer(new RoomPromotionMessageComposer(room, room.getPromotion()).compose());
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
