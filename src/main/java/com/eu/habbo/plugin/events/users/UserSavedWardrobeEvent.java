package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.WardrobeComponent;

/**
 * Created on 24-10-2015 13:04.
 */
public class UserSavedWardrobeEvent extends UserEvent
{
    public final WardrobeComponent.WardrobeItem wardrobeItem;

    /**
     * @param habbo The Habbo this event applies to.
     * @param wardrobeItem The wardrobe item this look is saved to.
     */
    public UserSavedWardrobeEvent(Habbo habbo, WardrobeComponent.WardrobeItem wardrobeItem)
    {
        super(habbo);
        this.wardrobeItem = wardrobeItem;
    }
}
