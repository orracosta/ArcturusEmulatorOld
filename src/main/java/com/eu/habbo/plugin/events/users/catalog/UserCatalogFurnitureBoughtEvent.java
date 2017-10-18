package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.events.users.UserEvent;
import gnu.trove.set.hash.THashSet;

public class UserCatalogFurnitureBoughtEvent extends UserCatalogEvent
{
    /**
     * List of items bought.
     */
    public final THashSet<HabboItem> furniture;

    /**
     * This event is triggered whenever someone buys a furniture.
     * Cancelling this event will not prevent the furniture from being bought.
     * @param habbo     The Habbo this event applies to.
     * @param catalogItem The CatalogItem this event applies to.
     * @param furniture The furniture this event applies to.
     */
    public UserCatalogFurnitureBoughtEvent( Habbo habbo, CatalogItem catalogItem ,THashSet<HabboItem> furniture)
    {
        super(habbo, catalogItem);

        this.furniture = furniture;
    }
}
