package com.eu.habbo.habbohotel.items;

import com.eu.habbo.habbohotel.users.HabboItem;

public class ItemInteraction
{
    private final String name;
    private final Class<? extends HabboItem> type;

    /**
     * Creates an new ItemInteraction that will be used to load custom ItemInteractions.
     * Note that the name (item_interaction_type) must be unique. Failing to do so will result
     * into an launch failure at startup.
     * @param name The name of this item interaction. Do not use capitals or spaces.
     * @param type The interaction class that is linked and will be instantiated for new items.
     */
    public ItemInteraction(String name, Class<? extends HabboItem> type)
    {
        this.name = name;
        this.type = type;
    }

    /**
     * @return The class that is lined to this interaction.
     */
    public Class<? extends HabboItem> getType()
    {
        return this.type;
    }

    /**
     * Should only be used upon startup and never get called outside of the emulator.
     * Use getType() in order to identify the correct interactions!
     * @return The interaction name as defined in the database.
     */
    public String getName()
    {
        return this.name;
    }
}
