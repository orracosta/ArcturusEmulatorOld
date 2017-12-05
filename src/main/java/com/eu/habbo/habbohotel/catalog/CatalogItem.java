package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class CatalogItem implements ISerialize, Runnable, Comparable<CatalogItem>
{
    /**
     * Unique identifier.
     */
    protected int id;

    /**
     * Page where this item will be displayed.
     */
    protected int pageId;

    /**
     * String representation of the items that are displayed.
     */
    protected String itemId;

    /**
     * Catalog item name.
     */
    protected String name;

    /**
     * The amount of credits this item can be purchased for.
     */
    protected int credits;

    /**
     * The amount of points this item can be purchased for.
     */
    protected int points;

    /**
     * The seasonal currency that is used in order to buy this item.
     * Defaults to pixels at 0.
     */
    protected short pointsType;

    /**
     * The amount of times this item will be given when purchased.
     */
    protected int amount;

    /**
     * If this item can be gifted.
     */
    protected boolean allowGift = false;

    /**
     * The total limited stack of this catalog item.
     */
    protected int limitedStack;

    /**
     * The amount of items that have been sold in this limited stack.
     */
    protected int limitedSells;

    /**
     * Extradata can be used to hold more data or set default data for the items bought.
     */
    protected String extradata;

    /**
     * Determines if this item can only be bought by people that have Habbo Club.
     */
    protected boolean clubOnly;

    /**
     * Determines if multiple purchases and thus discount is available.
     */
    protected boolean haveOffer;

    /**
     * The search offer id linked to this catalog item.
     */
    protected int offerId;

    /**
     * Flag to mark this item requiring an update to the database.
     */
    protected boolean needsUpdate;

    /**
     * Contains the amount of items in the bundle.
     */
    protected HashMap<Integer, Integer> bundle;

    public CatalogItem(ResultSet set) throws SQLException
    {
        this.load(set);
        this.needsUpdate = false;
    }

    /**
     * Updaes the CatalogItem with the given resultset.
     * @param set The ResultSet to update the CatalogItem with.
     * @throws SQLException
     */
    public void update(ResultSet set) throws SQLException
    {
        this.load(set);
    }

    private void load(ResultSet set) throws SQLException
    {
        this.id           = set.getInt("id");
        this.pageId       = set.getInt("page_id");
        this.itemId       = set.getString("item_Ids");
        this.name         = set.getString("catalog_name");
        this.credits      = set.getInt("cost_credits");
        this.points       = set.getInt("cost_points");
        this.pointsType   = set.getShort("points_type");
        this.amount       = set.getInt("amount");
        this.limitedStack = set.getInt("limited_stack");
        this.limitedSells = set.getInt("limited_sells");
        this.extradata    = set.getString("extradata");
        this.clubOnly     = set.getBoolean("club_only");
        this.haveOffer    = set.getBoolean("have_offer");
        this.offerId      = set.getInt("offer_id");

        this.bundle = new HashMap<>();
        this.loadBundle();
    }

    /**
     * @return Unique identifier.
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * @return Page where this item will be displayed.
     */
    public int getPageId()
    {
        return this.pageId;
    }

    /**
     * @param pageId Thenew page id to set for the item of where it will be displayed.
     */
    public void setPageId(int pageId)
    {
        this.pageId = pageId;
    }

    /**
     * @return String representation of the items that are displayed.
     */
    public String getItemId()
    {
        return this.itemId;
    }

    /**
     * @param itemId String representation of the items that are displayed.
     */
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    /**
     * @return Catalog item name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return The amount of credits this item can be purchased for.
     */
    public int getCredits()
    {
        return this.credits;
    }

    /**
     * @return The amount of points this item can be purchased for.
     */
    public int getPoints()
    {
        return this.points;
    }

    /**
     * @return The seasonal currency that is used in order to buy this item.
     *         Defaults to pixels at 0.
     */
    public int getPointsType()
    {
        return this.pointsType;
    }

    /**
     * @return The amount of times this item will be given when purchased.
     */
    public int getAmount()
    {
        return this.amount;
    }

    /**
     * @return The total limited stack of this catalog item.
     */
    public int getLimitedStack()
    {
        return this.limitedStack;
    }

    /**
     * @return The amount of items that have been sold in this limited stack.
     */
    public int getLimitedSells()
    {
        CatalogLimitedConfiguration ltdConfig = Emulator.getGameEnvironment().getCatalogManager().getLimitedConfig(this);

        if (ltdConfig != null)
        {
            return this.limitedStack - ltdConfig.available();
        }

        return this.limitedStack;
    }

    /**
     * @return Extradata can be used to hold more data or set default data for the items bought.
     */
    public String getExtradata()
    {
        return this.extradata;
    }

    /**
     * @return Determines if this item can only be bought by people that have Habbo Club.
     */
    public boolean isClubOnly()
    {
        return this.clubOnly;
    }

    /**
     * @return Determines if multiple purchases and thus discount is available.
     */
    public boolean isHaveOffer()
    {
        return this.haveOffer;
    }

    /**
     * @return The search offer id linked to this catalog item.
     */
    public int getOfferId()
    {
        return this.offerId;
    }

    /**
     * @return Returns True if this item has a limited stack.
     */
    public boolean isLimited()
    {
        return this.limitedStack > 0;
    }

    /**
     * Sell a limited item.
     */
    public synchronized void sellRare()
    {
        this.limitedSells++;

        this.needsUpdate = true;

        if(this.limitedSells == this.limitedStack)
        {
            Emulator.getGameEnvironment().getCatalogManager().moveCatalogItem(this, Emulator.getConfig().getInt("catalog.ltd.page.soldout"));
        }

        Emulator.getThreading().run(this);
    }

    /**
     * @return Return all BaseItems being sold.
     */
    public THashSet<Item> getBaseItems()
    {
        THashSet<Item> items = new THashSet<>();

        if(!this.itemId.isEmpty())
        {
            String[] itemIds = this.itemId.split(";");

            for (String itemId : itemIds)
            {
                if (itemId.isEmpty())
                    continue;

                if (itemId.contains(":"))
                {
                    itemId = itemId.split(":")[0];
                }

                int identifier = Integer.parseInt(itemId);
                if (identifier > 0)
                {
                    Item item = Emulator.getGameEnvironment().getItemManager().getItem(identifier);

                    if (item != null)
                        items.add(item);
                }
            }
        }

        return items;
    }

    /**
     * @return The amount of items being sold.
     */
    public int getItemAmount(int id)
    {
        if(this.bundle.containsKey(id))
            return this.bundle.get(id);
        else
            return this.amount;
    }

    /**
     * @return The bundle items.
     */
    public HashMap<Integer, Integer> getBundle()
    {
        return this.bundle;
    }

    /**
     * Loads the items in a bundle.
     */
    public void loadBundle()
    {
        int intItemId;

        if(this.itemId.contains(";"))
        {
            try
            {
                String[] itemIds = this.itemId.split(";");

                for (String itemId : itemIds)
                {
                    if (itemId.contains(":"))
                    {
                        String[] data = itemId.split(":");
                        if (data.length > 1 && Integer.parseInt(data[0]) > 0 && Integer.parseInt(data[1]) > 0)
                        {
                            this.bundle.put(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
                        }
                    } else
                    {
                        if (!itemId.isEmpty())
                        {
                            intItemId = (Integer.parseInt(itemId));
                            this.bundle.put(intItemId, 1);
                        }
                    }
                }
            } catch (Exception e)
            {
                Emulator.getLogging().logDebugLine("Failed to load " + itemId);
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                Item item = Emulator.getGameEnvironment().getItemManager().getItem(Integer.valueOf(this.itemId));

                if (item != null)
                {
                    this.allowGift = item.allowGift();
                }
            }
            catch (Exception e)
            {}
        }
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt(this.getId());
        message.appendString(this.getName());
        message.appendBoolean(false);
        message.appendInt(this.getCredits());
        message.appendInt(this.getPoints());
        message.appendInt(this.getPointsType());
        message.appendBoolean(this.allowGift); //Can gift

        THashSet<Item> items = this.getBaseItems();

        message.appendInt(items.size());

        for(Item item : items)
        {
            message.appendString(item.getType().code.toLowerCase());

            if(item.getType() == FurnitureType.BADGE)
            {
                message.appendString(item.getName());
            }
            else
            {
                message.appendInt(item.getSpriteId());

                if(this.getName().contains("wallpaper_single") || this.getName().contains("floor_single") || this.getName().contains("landscape_single"))
                {
                    message.appendString(this.getName().split("_")[2]);
                }
                else if(item.getName().contains("bot") && item.getType() == FurnitureType.ROBOT)
                {
                    boolean lookFound = false;
                    for (String s : this.getExtradata().split(";"))
                    {
                        if (s.startsWith("figure:"))
                        {
                            lookFound = true;
                            message.appendString(s.replace("figure:", ""));
                            break;
                        }
                    }

                    if (!lookFound)
                    {
                        message.appendString(this.getExtradata());
                    }
                }
                else if(item.getType() == FurnitureType.ROBOT)
                {
                    message.appendString(this.getExtradata());
                }
                else if(item.getName().equalsIgnoreCase("poster"))
                {
                    message.appendString(this.getExtradata());
                }
                else if(this.getName().startsWith("SONG "))
                {
                    message.appendString(this.getExtradata());
                }
                else
                {
                    message.appendString("");
                }
                message.appendInt(this.getItemAmount(item.getId()));
                message.appendBoolean(this.isLimited());
                if(this.isLimited())
                {
                    message.appendInt(this.getLimitedStack());
                    message.appendInt(this.getLimitedStack() - this.getLimitedSells());
                }
            }
        }

        message.appendInt32(this.clubOnly);
        message.appendBoolean(haveOffer(this));
        message.appendBoolean(false); //unknown
        message.appendString(this.name + ".png");
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE catalog_items SET limited_sells = ?, page_id = ? WHERE id = ?"))
            {
                statement.setInt(1, this.getLimitedSells());
                statement.setInt(2, this.pageId);
                statement.setInt(3, this.getId());
                statement.execute();
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            this.needsUpdate = false;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(CatalogItem catalogItem) {
        return this.getId() - catalogItem.getId();
    }

    /**
     * Does additional checks to see if an item has offers enabled.
     * @param item The item to check
     * @return True if the item has offers enabled.
     */
    private static boolean haveOffer(CatalogItem item)
    {
        if(!item.haveOffer)
            return false;

        for(Item i : item.getBaseItems())
        {
            if(i.getName().toLowerCase().startsWith("cf_") || i.getName().toLowerCase().startsWith("cfc_") || i.getName().toLowerCase().startsWith("rentable_bot"))
                return false;
        }

        if(item.getName().toLowerCase().startsWith("cf_") || item.getName().toLowerCase().startsWith("cfc_"))
            return false;

        if(item.isLimited())
            return false;

        if(item.getName().toLowerCase().startsWith("rentable_bot_"))
            return false;

        if(item.getAmount() != 1)
            return false;

        return item.bundle.size() <= 1;
    }
}
