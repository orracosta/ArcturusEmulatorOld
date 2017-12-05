package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;

import java.util.List;

public class UserCatalogItemPurchasedEvent extends UserCatalogEvent
{
    /**
     * The items that will be purchased.
     * Modify the list to add more furniture.
     */
    public final THashSet<HabboItem> itemsList;

    /**
     * Total amount of credits.
     */
    public int totalCredits;

    /**
     * Total amount of points.
     */
    public int totalPoints;

    /**
     * Badges
     */
    public List<String> badges;

    /**
     * @param habbo The Habbo this event applies to.
     * @param catalogItem The CatalogItem this event applies to.
     * @param itemsList The items that will be purchased. Modify the list to add more furniture.
     * @param totalCredits Total amount of credits.
     * @param totalPoints Total amount of points.
     */
    public UserCatalogItemPurchasedEvent(Habbo habbo, CatalogItem catalogItem, THashSet<HabboItem> itemsList, int totalCredits, int totalPoints, List<String> badges)
    {
        super(habbo, catalogItem);

        this.itemsList    = itemsList;
        this.totalCredits = totalCredits;
        this.totalPoints  = totalPoints;
        this.badges       = badges;
    }
}