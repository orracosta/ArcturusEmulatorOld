package com.eu.habbo.plugin.events.users.catalog;

import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserCatalogEvent extends UserEvent
{
    /**
     * The CatalogItem this event applies to.
     */
    public CatalogItem catalogItem;

    /**
     * @param habbo The Habbo this event applies to.
     * @param catalogItem The CatalogItem this event applies to.
     */
    public UserCatalogEvent(Habbo habbo, CatalogItem catalogItem)
    {
        super(habbo);

        this.catalogItem = catalogItem;
    }
}