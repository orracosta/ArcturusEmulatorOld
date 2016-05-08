package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created on 28-8-2014 11:34.
 */
public class CatalogItem implements ISerialize, Runnable, Comparable<CatalogItem>
{
    protected int id;
    protected int pageId;
    protected String itemId;
    protected String name;
    protected int credits;
    protected int points;
    protected short pointsType;
    protected int amount;
    protected int limitedStack;
    protected int limitedSells;
    protected String extradata;
    protected String badge;
    protected boolean clubOnly;
    protected boolean haveOffer;
    protected int offerId;

    protected boolean needsUpdate;
    protected boolean hasBadge;

    protected HashMap<Integer, Integer> bundle;

    public CatalogItem(ResultSet set) throws SQLException
    {
        this.load(set);
        this.needsUpdate = false;
    }

    public void update(ResultSet set) throws SQLException
    {
        this.load(set);
    }

    private void load(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.pageId = set.getInt("page_id");
        this.itemId = set.getString("item_Ids");
        this.name = set.getString("catalog_name");
        this.credits = set.getInt("cost_credits");
        this.points = set.getInt("cost_points");
        this.pointsType = set.getShort("points_type");
        this.amount = set.getInt("amount");
        this.limitedStack = set.getInt("limited_stack");
        this.limitedSells = set.getInt("limited_sells");
        this.extradata = set.getString("extradata");
        this.badge = set.getString("badge");
        this.clubOnly = set.getBoolean("club_only");
        this.haveOffer = set.getBoolean("have_offer");
        this.offerId = set.getInt("offer_id");

        this.bundle = new HashMap<Integer, Integer>();
        this.loadBundle();
        this.hasBadge = !this.badge.isEmpty();
    }

    public int getId()
    {
        return id;
    }

    public int getPageId()
    {
        return pageId;
    }

    public void setPageId(int pageId)
    {
        this.pageId = pageId;
    }

    public String getItemId()
    {
        return this.itemId;
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    public String getName()
    {
        return name;
    }

    public int getCredits()
    {
        return credits;
    }

    public int getPoints()
    {
        return points;
    }

    public int getPointsType()
    {
        return pointsType;
    }

    public int getAmount()
    {
        return amount;
    }

    public int getLimitedStack()
    {
        return limitedStack;
    }

    public int getLimitedSells()
    {
        return limitedSells;
    }

    public String getExtradata()
    {
        return extradata;
    }

    public String getBadge()
    {
        return this.badge;
    }

    public boolean hasBadge() { return this.hasBadge; }

    public boolean isClubOnly()
    {
        return clubOnly;
    }

    public boolean isHaveOffer()
    {
        return haveOffer;
    }

    public int getOfferId()
    {
        return this.offerId;
    }

    public boolean isLimited()
    {
        return this.limitedStack > 0;
    }

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

    public THashSet<Item> getBaseItems()
    {
        THashSet<Item> items = new THashSet<Item>();

        if(!this.itemId.isEmpty())
        {
            String[] itemIds = this.itemId.split(";");

            for (String itemId : itemIds)
            {
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

    public int getItemAmount(int id)
    {
        if(this.bundle.containsKey(id))
            return this.bundle.get(id);
        else
            return this.amount;
    }

    public HashMap<Integer, Integer> getBundle()
    {
        return this.bundle;
    }

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
                        intItemId = (Integer.parseInt(itemId));
                        this.bundle.put(intItemId, 1);
                    }
                }
            } catch (Exception e)
            {
                Emulator.getLogging().logDebugLine("Failed to load " + itemId);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt32(this.getId());
        message.appendString(this.getName());
        message.appendBoolean(false);
        message.appendInt32(this.getCredits());
        message.appendInt32(this.getPoints());
        message.appendInt32(this.getPointsType());
        message.appendBoolean(true); //Can gift

        THashSet<Item> items = this.getBaseItems();

        if(this.badge.isEmpty() || this.badge.length() == 0)
        {
            message.appendInt32(items.size());
        }
        else
        {
            message.appendInt32(items.size() + 1);
            message.appendString("b");
            message.appendString(this.getBadge());
        }
        for(Item item : items)
        {
            message.appendString(item.getType());

            if(item.getType().equals("b"))
            {
                message.appendString("RADZZ");
            }
            else
            {
                message.appendInt32(item.getSpriteId());

                if(this.getName().contains("wallpaper_single") || this.getName().contains("floor_single") || this.getName().contains("landscape_single"))
                {
                    message.appendString(this.getName().split("_")[2]);
                }
                else if(item.getName().equalsIgnoreCase("rentable_bot"))
                {
                    message.appendString(this.getExtradata());
                }
                else if(item.getType().toLowerCase().equals("r"))
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
                message.appendInt32(this.getItemAmount(item.getId()));
                message.appendBoolean(this.isLimited());
                if(this.isLimited())
                {
                    message.appendInt32(this.getLimitedSells());
                    message.appendInt32(this.getLimitedStack() - this.getLimitedSells());
                }
            }
        }

        message.appendInt32(this.clubOnly);
        message.appendBoolean(haveOffer(this));
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE catalog_items SET limited_sells = ?, page_id = ? WHERE id = ?");
                statement.setInt(1, this.getLimitedSells());
                statement.setInt(2, this.pageId);
                statement.setInt(3, this.getId());
                statement.execute();
                statement.close();
                statement.getConnection().close();
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
