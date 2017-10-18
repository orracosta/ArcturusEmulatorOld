package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.catalog.ClubOffer;
import com.eu.habbo.habbohotel.catalog.layouts.ClubBuyLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RecentPurchasesLayout;
import com.eu.habbo.habbohotel.catalog.layouts.RoomBundleLayout;
import com.eu.habbo.habbohotel.catalog.layouts.VipBuyLayout;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.*;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.HotelWillCloseInMinutesComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.users.*;
import com.eu.habbo.threading.runnables.ShutdownEmulator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;

public class CatalogBuyItemEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (ShutdownEmulator.timestamp > 0)
        {
            this.client.sendResponse(new HotelWillCloseInMinutesComposer((ShutdownEmulator.timestamp - Emulator.getIntUnixTimestamp()) / 60));
            return;
        }

        int pageId = this.packet.readInt();
        int itemId = this.packet.readInt();
        String extraData = this.packet.readString();
        int count = this.packet.readInt();

        try
        {
            if (this.client.getHabbo().getInventory().getItemsComponent().itemCount() > HabboInventory.MAXIMUM_ITEMS)
            {
                this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR).compose());
                this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("inventory.full")));
                return;
            }
        }
        catch (Exception e)
        {
            this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR).compose());
        }

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

                if (!this.client.getHabbo().hasPermission("acc_infinite_credits"))
                {
                    this.client.getHabbo().getHabboInfo().addCredits(-item[0].getCredits());
                }

                if (!this.client.getHabbo().hasPermission("acc_inifinte_points"))
                {
                    this.client.getHabbo().getHabboInfo().addCurrencyAmount(item[0].getPointsType(), -item[0].getPoints());
                }

                this.client.sendResponse(new PurchaseOKComposer());

                final boolean[] badgeFound = {false};
                item[0].getBaseItems().stream().filter(i -> i.getType() == FurnitureType.BADGE).forEach(i -> {
                    if (!this.client.getHabbo().getInventory().getBadgesComponent().hasBadge(i.getName()))
                    {
                        HabboBadge badge = new HabboBadge(0, i.getName(), 0, this.client.getHabbo());
                        Emulator.getThreading().run(badge);
                        this.client.getHabbo().getInventory().getBadgesComponent().addBadge(badge);
                        this.client.sendResponse(new AddUserBadgeComposer(badge));
                        THashMap<String, String> keys = new THashMap<String, String>();
                        keys.put("display", "BUBBLE");
                        keys.put("image", "${image.library.url}album1584/" + badge.getCode() + ".gif");
                        keys.put("message", Emulator.getTexts().getValue("commands.generic.cmd_badge.received"));
                        this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.RECEIVED_BADGE.key, keys)); //:test 1992 s:npc.gift.received i:2 s:npc_name s:Admin s:image s:${image.library.url}album1584/ADM.gif);
                    }
                    else
                    {
                        badgeFound[0] = true;
                    }
                });

                if (badgeFound[0])
                {
                    this.client.getHabbo().getClient().sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.ALREADY_HAVE_BADGE));
                }

                return;
            }
        }

        if(page == null)
        {
            this.client.sendResponse(new AlertPurchaseFailedComposer(AlertPurchaseFailedComposer.SERVER_ERROR).compose());
            return;
        }

        if(page.getRank() > this.client.getHabbo().getHabboInfo().getRank().getId())
        {
            this.client.sendResponse(new AlertPurchaseUnavailableComposer(AlertPurchaseUnavailableComposer.ILLEGAL));
            return;
        }

        if(page instanceof ClubBuyLayout || page instanceof VipBuyLayout)
        {
            ClubOffer item = Emulator.getGameEnvironment().getCatalogManager().clubOffers.get(itemId);

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
                totalDays += item.getDays();
                totalCredits += item.getCredits();
                totalDuckets += item.getPoints();
            }

            if(totalDays > 0)
            {
                if(this.client.getHabbo().getHabboInfo().getCurrencyAmount(item.getPointsType()) < totalDuckets)
                    return;

                if (this.client.getHabbo().getHabboInfo().getCredits() < totalCredits)
                    return;

                if (!this.client.getHabbo().hasPermission("acc_infinite_credits"))
                    this.client.getHabbo().getHabboInfo().addCredits(-totalCredits);

                if (!this.client.getHabbo().hasPermission("acc_infinite_points"))
                    this.client.getHabbo().getHabboInfo().addCurrencyAmount(item.getPointsType(), -totalDuckets);

                if(this.client.getHabbo().getHabboStats().getClubExpireTimestamp() <= Emulator.getIntUnixTimestamp())
                    this.client.getHabbo().getHabboStats().setClubExpireTimestamp(Emulator.getIntUnixTimestamp());

                this.client.getHabbo().getHabboStats().setClubExpireTimestamp(this.client.getHabbo().getHabboStats().getClubExpireTimestamp() + (totalDays * 86400));
                this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));
                this.client.sendResponse(new UserClubComposer(this.client.getHabbo()));

                if (totalCredits > 0)
                    this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));

                if (totalDuckets > 0)
                    this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));

                this.client.sendResponse(new PurchaseOKComposer(null));
                this.client.sendResponse(new InventoryRefreshComposer());
            }
            return;
        }

        CatalogItem item;

        if(page instanceof RecentPurchasesLayout)
            item = this.client.getHabbo().getHabboStats().getRecentPurchases().get(itemId);
        else
            item = page.getCatalogItem(itemId);

        Emulator.getGameEnvironment().getCatalogManager().purchaseItem(page, item, this.client.getHabbo(), count, extraData, false);

    }
}
