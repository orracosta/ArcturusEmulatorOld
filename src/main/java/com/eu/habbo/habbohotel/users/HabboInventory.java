package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceOffer;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlaceState;
import com.eu.habbo.habbohotel.users.inventory.*;
import gnu.trove.set.hash.THashSet;

public class HabboInventory
{
    //Configuration. Loaded from database & updated accordingly.
    public static int MAXIMUM_ITEMS = 10000;

    private WardrobeComponent wardrobeComponent;
    private BadgesComponent badgesComponent;
    private BotsComponent botsComponent;
    private EffectsComponent effectsComponent;
    private ItemsComponent itemsComponent;
    private PetsComponent petsComponent;

    private final THashSet<MarketPlaceOffer> items;

    public HabboInventory(Habbo habbo)
    {
        try
        {
            this.badgesComponent = new BadgesComponent(habbo);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        try
        {
            this.botsComponent = new BotsComponent(habbo);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        try
        {
            this.effectsComponent = new EffectsComponent(habbo);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        try
        {
            this.itemsComponent = new ItemsComponent(habbo);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        try
        {
            this.petsComponent = new PetsComponent(habbo);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        try
        {
            this.wardrobeComponent = new WardrobeComponent(habbo);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        this.items = MarketPlace.getOwnOffers(habbo);
    }

    public BadgesComponent getBadgesComponent()
    {
        return this.badgesComponent;
    }

    public BotsComponent getBotsComponent()
    {
        return this.botsComponent;
    }

    public EffectsComponent getEffectsComponent()
    {
        return this.effectsComponent;
    }

    public ItemsComponent getItemsComponent()
    {
        return this.itemsComponent;
    }

    public PetsComponent getPetsComponent()
    {
        return this.petsComponent;
    }

    public WardrobeComponent getWardrobeComponent()
    {
        return this.wardrobeComponent;
    }

    public void dispose()
    {
        this.badgesComponent.dispose();
        this.botsComponent.dispose();
        this.effectsComponent.dispose();
        this.itemsComponent.dispose();
        this.petsComponent.dispose();
        this.wardrobeComponent.dispose();

        this.badgesComponent = null;
        this.botsComponent = null;
        this.effectsComponent = null;
        this.itemsComponent = null;
        this.petsComponent = null;
        this.wardrobeComponent = null;
    }

    public void addMarketplaceOffer(MarketPlaceOffer marketPlaceOffer)
    {
        this.items.add(marketPlaceOffer);
    }

    public void removeMarketplaceOffer(MarketPlaceOffer marketPlaceOffer)
    {
        this.items.remove(marketPlaceOffer);
    }

    public THashSet<MarketPlaceOffer> getMarketplaceItems()
    {
        return this.items;
    }

    public int getSoldPriceTotal()
    {
        int i = 0;
        for(MarketPlaceOffer offer : this.items)
        {
            if(offer.getState().equals(MarketPlaceState.SOLD))
            {
                i+= offer.getPrice();
            }
        }
        return i;
    }

    public MarketPlaceOffer getOffer(int id)
    {
        for(MarketPlaceOffer offer : this.items)
        {
            if(offer.getOfferId() == id)
                return offer;
        }

        return null;
    }
}
