package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.catalog.layouts.ClubBuyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RecentPurchasesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RoomBundleLayout;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.users.*;
import gnu.trove.procedure.TObjectProcedure;

public class CatalogBuyItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int pageId = this.packet.readInt();
        int itemId = this.packet.readInt();
        String extraData = this.packet.readString();
        int count = this.packet.readInt();

        CatalogPage page = null;

        if(pageId == -12345678)
        {
            for(CatalogPage p : Emulator.getGameEnvironment().getCatalogManager().getCatalogPages(-1, this.client.getHabbo()))
            {
                if(p.getCatalogItem(itemId) != null)
                {
                    page = p;
                    break;
                }
                else
                {
                    for(CatalogPage p2 : Emulator.getGameEnvironment().getCatalogManager().getCatalogPages(p.getId(), this.client.getHabbo()))
                    {
                        if(p2.getCatalogItem(itemId) != null)
                        {
                            page = p2;
                            break;
                        }
                    }
                }
            }
        }
        else
        {
            page = Emulator.getGameEnvironment().getCatalogManager().catalogPages.get(pageId);

            if(page instanceof RoomBundleLayout)
            {
                final CatalogItem[] item = new CatalogItem[1];
                page.getCatalogItems().forEachValue(new TObjectProcedure<CatalogItem>()
                {
                    @Override
                    public boolean execute(CatalogItem object)
                    {
                        item[0] = object;
                        return false;
                    }
                });

                if(item[0] == null || item[0].getCredits() > this.client.getHabbo().getHabboInfo().getCredits() || item[0].getPoints() > this.client.getHabbo().getHabboInfo().getCurrencyAmount(item[0].getPointsType()))
                {
                    this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR));
                    return;
                }

                ((RoomBundleLayout)page).buyRoom(this.client.getHabbo());

                this.client.getHabbo().getHabboInfo().addCredits(-item[0].getCredits());
                this.client.getHabbo().getHabboInfo().addCurrencyAmount(item[0].getPointsType(), -item[0].getPoints());
                this.client.sendResponse(new PurchaseOKComposer());

                if(item[0].hasBadge())
                {
                    if(!this.client.getHabbo().getHabboInventory().getBadgesComponent().hasBadge(item[0].getBadge()))
                    {
                        HabboBadge badge = new HabboBadge(0, item[0].getBadge(), 0, this.client.getHabbo());
                        Emulator.getThreading().run(badge);
                        this.client.getHabbo().getHabboInventory().getBadgesComponent().addBadge(badge);
                    }
                    else
                    {
                        this.client.getHabbo().getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.ALREADY_HAVE_BADGE));
                    }
                }

                return;
            }
        }

        if(page == null)
        {
            this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR).compose());
            return;
        }

        if(page.getRank() > this.client.getHabbo().getHabboInfo().getRank())
        {
            this.client.sendResponse(new AlertPurchaseUnavailableComposer(AlertPurchaseUnavailableComposer.ILLEGAL));
            return;
        }

        if(page instanceof ClubBuyLayout)
        {
            CatalogItem item = Emulator.getGameEnvironment().getCatalogManager().getClubItem(itemId);

            if(item == null)
            {
                this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR).compose());
                return;
            }

            int totalDays = 0;
            int totalCredits = 0;
            int totalDuckets = 0;

            for(int i = 0; i < count; i++)
            {
                String[] data = item.getName().split("_");

                if(data[2].equalsIgnoreCase("day"))
                {
                    totalDays = Integer.valueOf(data[3]);
                }
                else if(data[2].equalsIgnoreCase("month"))
                {
                    totalDays = Integer.valueOf(data[3]) * 31;
                }
                else if(data[2].equalsIgnoreCase("year"))
                {
                    totalDays = Integer.valueOf(data[3]) * 365;
                }

                totalCredits += item.getCredits();
                totalDuckets += item.getPoints();
            }

            if(totalDays > 0)
            {
                if(this.client.getHabbo().getHabboInfo().getCurrencyAmount(item.getPointsType()) < totalDuckets)
                    return;

                if (this.client.getHabbo().getHabboInfo().getCredits() < totalCredits)
                    return;

                this.client.getHabbo().getHabboInfo().addCurrencyAmount(item.getPointsType(), -totalDuckets);

                this.client.getHabbo().getHabboInfo().addCredits(-totalCredits);

                if(this.client.getHabbo().getHabboStats().getClubExpireTimestamp() <= Emulator.getIntUnixTimestamp())
                    this.client.getHabbo().getHabboStats().setClubExpireTimestamp(Emulator.getIntUnixTimestamp());

                this.client.getHabbo().getHabboStats().setClubExpireTimestamp(this.client.getHabbo().getHabboStats().getClubExpireTimestamp() + (totalDays * 86400));
                this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));
                this.client.sendResponse(new UserClubComposer(this.client.getHabbo()));

                if (totalCredits > 0)
                    this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));

                if (totalDuckets > 0)
                    this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));

                this.client.sendResponse(new PurchaseOKComposer(item, null));
                this.client.sendResponse(new InventoryRefreshComposer());
            }
            return;
        }

        CatalogItem item;

        if(page instanceof RecentPurchasesLayout)
            item = this.client.getHabbo().getHabboStats().getRecentPurchases().get(itemId);
        else
            item = page.getCatalogItem(itemId);

        Emulator.getGameEnvironment().getCatalogManager().purchaseItem(page, item, this.client.getHabbo(), count, extraData);

    }
}
