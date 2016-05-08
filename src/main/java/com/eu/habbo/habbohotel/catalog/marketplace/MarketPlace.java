package com.eu.habbo.habbohotel.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceBuyErrorComposer;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceCancelSaleComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 1-11-2014 13:06.
 *
 * Note: A lot of data in here is not cached in the emulator but rather loaded from the database.
 * If you have a big hotel and the marketplace is used frequently this may cause some lagg.
 * If you have issues with that please let us know so we can see if we need to modify the Marketplace so
 * it caches the data in the Emulator.
 */
public class MarketPlace
{
    /**
     * @param habbo The Habbo to lookup items for.
     * @return The items that are put on marketplace by the given Habbo.
     */
    public static THashSet<MarketPlaceOffer> getOwnOffers(Habbo habbo)
    {
        THashSet<MarketPlaceOffer> offers = new THashSet<MarketPlaceOffer>();
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT items_base.type AS type, items.item_id AS base_item_id, items.limited_data AS ltd_data, marketplace_items.* FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE marketplace_items.user_id = ?");
            statement.setInt(1, habbo.getHabboInfo().getId());
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                offers.add(new MarketPlaceOffer(set, true));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return  offers;
    }

    /**
     * Removes an offer from the marketplace.
     * @param habbo The Habbo that owns the item.
     * @param offerId The offer id.
     */
    public static void takeBackItem(Habbo habbo, int offerId)
    {
        MarketPlaceOffer offer = habbo.getHabboInventory().getOffer(offerId);

        takeBackItem(habbo, offer);
    }

    /**
     * Removes an offer from the marketplace.
     * @param habbo The Habbo that owns the item.
     * @param offer The offer.
     */
    private static void takeBackItem(Habbo habbo, MarketPlaceOffer offer)
    {
        if(offer != null && habbo.getHabboInventory().getMarketplaceItems().contains(offer))
        {
            try
            {
                PreparedStatement ownerCheck = Emulator.getDatabase().prepare("SELECT user_id FROM marketplace_items WHERE id = ?");
                ownerCheck.setInt(1, offer.getOfferId());
                ResultSet ownerSet = ownerCheck.executeQuery();
                ownerSet.last();

                if(ownerSet.getRow() == 0)
                {
                    ownerSet.close();
                    ownerCheck.close();
                    ownerCheck.getConnection().close();
                    return;
                }

                habbo.getHabboInventory().removeMarketplaceOffer(offer);
                PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM marketplace_items WHERE id = ?");
                statement.setInt(1, offer.getOfferId());
                statement.execute();
                statement.close();
                statement.getConnection().close();

                PreparedStatement updateItems = Emulator.getDatabase().prepare("UPDATE items SET user_id = ? WHERE id = ? LIMIT 1");
                updateItems.setInt(1, habbo.getHabboInfo().getId());
                updateItems.setInt(2, offer.getSoldItemId());
                updateItems.execute();
                updateItems.close();
                updateItems.getConnection().close();

                PreparedStatement selectItem = Emulator.getDatabase().prepare("SELECT * FROM items WHERE id = ? LIMIT 1");
                selectItem.setInt(1, offer.getSoldItemId());
                ResultSet set = selectItem.executeQuery();
                while(set.next())
                {
                    HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);
                    habbo.getHabboInventory().getItemsComponent().addItem(item);
                    habbo.getClient().sendResponse(new MarketplaceCancelSaleComposer(offer, true));
                    habbo.getClient().sendResponse(new AddHabboItemComposer(item));
                    habbo.getClient().sendResponse(new InventoryRefreshComposer());
                }
                set.close();
                selectItem.close();
                selectItem.getConnection().close();

                ownerCheck.close();
                ownerCheck.getConnection().close();
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
                habbo.getClient().sendResponse(new MarketplaceCancelSaleComposer(offer, false));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Searches the marketplace for specific items in the given price range.
     * @param minPrice The minimum price of the offers.
     * @param maxPrice The maximum price of the offers
     * @param search The search criteria for the offers.
     * @param sort The order the data should be sorted.
     * @return The result of the search.
     */
    public static THashSet<MarketPlaceOffer> getOffers(int minPrice, int maxPrice, String search, int sort)
    {
        THashSet<MarketPlaceOffer> offers = new THashSet<MarketPlaceOffer>();

        String query = "SELECT items_base.type AS type, items.item_id AS base_item_id, items.limited_data AS ltd_data, marketplace_items.*, COUNT(*) as number, AVG(price) as avg, MIN(price) as minPrice FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = ? AND timestamp >= ?";

        if(minPrice > 0)
        {
            query += " AND price >= " + minPrice;
        }
        if(maxPrice > 0 && maxPrice > minPrice)
        {
            query += " AND price <= " + maxPrice;
        }
        if(search.length() > 0)
        {
            query += " AND items_base.item_name LIKE ?";
        }

        query += " GROUP BY base_item_id, ltd_data";

        switch(sort)
        {
            case 2: query += " ORDER BY price ASC"; break;

            case 1:
            default: query += " ORDER BY price DESC"; break;
        }

        query += " LIMIT 250";

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare(query);
            statement.setInt(1, 1);
            statement.setInt(2, Emulator.getIntUnixTimestamp() - 172800);
            if(search.length() > 0)
                statement.setString(3, "%"+search+"%");

            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                offers.add(new MarketPlaceOffer(set, false));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return offers;
    }

    /**
     * Serializes the item info.
     * @param itemId The item id that should be looked up.
     * @param message The message the data should be appended to.
     */
    public static void serializeItemInfo(int itemId, ServerMessage message)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT avg(price) as price, COUNT(*) as sold, (datediff(NOW(), DATE(from_unixtime(timestamp)))) as day FROM marketplace_items INNER JOIN items ON items.id = marketplace_items.item_id INNER JOIN items_base ON items.item_id = items_base.id WHERE items.limited_data = '0:0' AND state = 2 AND items_base.sprite_id = ? AND DATE(from_unixtime(timestamp)) >= NOW() - INTERVAL 30 DAY GROUP BY DATE(from_unixtime(timestamp))");
            statement.setInt(1, itemId);
            ResultSet set = statement.executeQuery();

            message.appendInt32(avarageLastXDays(itemId, 7));//idk
            message.appendInt32(itemsOnSale(itemId)); //offcount to do!
            message.appendInt32(30); //days.

            set.last();
            message.appendInt32(set.getRow());
            set.beforeFirst();

            while(set.next())
            {
                message.appendInt32(-set.getInt("day"));
                message.appendInt32(set.getInt("price"));
                message.appendInt32(set.getInt("sold"));
            }

            message.appendInt32(1);
            message.appendInt32(itemId);

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * @param baseItemId The base item id that should be looked up. (items_base table)
     * @return The amount of items of this type are on sale.
     */
    public static int itemsOnSale(int baseItemId)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT COUNT(*) as number, AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = 1 AND timestamp >= ? AND items_base.sprite_id = ?");
            statement.setInt(1, Emulator.getIntUnixTimestamp() - 172800);
            statement.setInt(2, baseItemId);
            ResultSet set = statement.executeQuery();
            set.first();
            int number = set.getInt("number");

            set.close();
            statement.close();
            statement.getConnection().close();
            return number;
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return 0;
    }

    /**
     * @param baseItemId The base item id that should be looked up. (items_base table)
     * @param days The amount of days.
     * @return The average amount of coins this item was over the given amount of days.
     */
    private static int avarageLastXDays(int baseItemId, int days)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = 2 AND DATE(from_unixtime(timestamp)) >= NOW() - INTERVAL ? DAY AND items_base.sprite_id = ?");
            statement.setInt(1, days);
            statement.setInt(2, baseItemId);
            ResultSet set = statement.executeQuery();
            set.first();
            int avg =  set.getInt("avg");
            set.close();
            statement.close();
            statement.getConnection().close();
            return avg;
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return 0;
    }

    /**
     * Buys an item from the marketplace.
     * @param offerId The offer id that should be bought.
     * @param client The GameClient that buys it.
     */
    public static void buyItem(int offerId, GameClient client)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM marketplace_items WHERE id = ? LIMIT 1");
            statement.setInt(1, offerId);
            ResultSet set = statement.executeQuery();

            if(set.next())
            {
                PreparedStatement itemStatement = Emulator.getDatabase().prepare("SELECT * FROM items WHERE id = ? LIMIT 1");
                itemStatement.setInt(1, set.getInt("item_id"));
                ResultSet itemSet = itemStatement.executeQuery();
                itemSet.first();

                if(itemSet.getRow() > 0)
                {
                    if (set.getInt("state") != 1)
                    {
                        sendErrorMessage(client, set.getInt("item_id"), offerId);
                    } else if (set.getInt("price") > client.getHabbo().getHabboInfo().getCredits())
                    {
                        client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.NOT_ENOUGH_CREDITS, 0, offerId, set.getInt("price")));
                    }
                    else
                    {

                        PreparedStatement updateOffer = Emulator.getDatabase().prepare("UPDATE marketplace_items SET state = 2 WHERE id = ?");
                        updateOffer.setInt(1, offerId);
                        updateOffer.execute();

                        HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(itemSet);
                        item.setUserId(client.getHabbo().getHabboInfo().getId());
                        item.needsUpdate(true);
                        Emulator.getThreading().run(item);
                        client.getHabbo().getHabboInventory().getItemsComponent().addItem(item);
                        client.getHabbo().getHabboInfo().addCredits(-set.getInt("price"));
                        client.sendResponse(new UserCreditsComposer(client.getHabbo()));
                        client.sendResponse(new AddHabboItemComposer(item));
                        client.sendResponse(new InventoryRefreshComposer());
                        client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.REFRESH, 0, offerId, set.getInt("price")));

                        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(set.getInt("user_id"));
                        if (habbo != null)
                        {
                            habbo.getHabboInventory().getOffer(offerId).setState(MarketPlaceState.SOLD);
                        }
                        updateOffer.close();
                        updateOffer.getConnection().close();
                    }
                }

                itemSet.close();
                itemStatement.close();
                itemStatement.getConnection().close();
                set.close();
                statement.close();
                statement.getConnection().close();
                return;
            }

        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Sends an error message to the client. If the items was sold out or
     * if the previous one isn't on sale anymore then it shows the new price.
     * @param client The GameClient the messages should be send to.
     * @param baseItemId The Item id data selected from the marketplace_offers table.
     * @param offerId The id of the offer that was bought.
     * @throws SQLException
     */
    public static void sendErrorMessage(GameClient client, int baseItemId, int offerId) throws SQLException
    {
        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT marketplace_items.*, COUNT( * ) AS count\n" +
                "FROM marketplace_items\n" +
                "INNER JOIN items ON marketplace_items.item_id = items.id\n" +
                "INNER JOIN items_base ON items.item_id = items_base.id\n" +
                "WHERE items_base.sprite_id = ( \n" +
                "SELECT items_base.sprite_id\n" +
                "FROM items_base\n" +
                "WHERE items_base.id = ? LIMIT 1)\n" +
                "ORDER BY price DESC\n" +
                "LIMIT 1");
        statement.setInt(1, baseItemId);
        ResultSet countSet = statement.executeQuery();
        countSet.last();
        if (countSet.getRow() == 0)
            client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.SOLD_OUT, 0, offerId, 0));
        else
        {
            countSet.first();
            client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.UPDATES, countSet.getInt("count"), countSet.getInt("id"), countSet.getInt("price")));
        }
        countSet.close();
        statement.close();
        statement.getConnection().close();
    }

    /**
     * Sells an item to the marketplace.
     * @param client The GameClient that sells it.
     * @param item The HabboItem that is being sold.
     * @param price The price it is being sold for.
     * @return Whether it has succesfully been posted to the marketplace.
     */
    public static boolean sellItem(GameClient client, HabboItem item, int price)
    {
        if(item == null || client == null)
            return false;

        try
        {
            MarketPlaceOffer offer = new MarketPlaceOffer(item, calculateCommision(price), client.getHabbo());

            if(offer != null)
            {
                client.getHabbo().getHabboInventory().addMarketplaceOffer(offer);

                client.getHabbo().getHabboInventory().getItemsComponent().removeHabboItem(item);
                client.sendResponse(new RemoveHabboItemComposer(item.getId()));
                client.sendResponse(new InventoryRefreshComposer());
                item.setUserId(-1);
                item.needsUpdate(true);
                Emulator.getThreading().run(item);
            }
        }
        catch (SQLException e)
        {
            return false;
        }

        return true;
    }

    /**
     * Claims all the credits that are gained from selling items on the marketplace.
     * @param client The GameClient the credits should be checked for.
     */
    public static void getCredits(GameClient client)
    {
        int credits = 0;

        THashSet<MarketPlaceOffer> offers = new THashSet<MarketPlaceOffer>();
        offers.addAll(client.getHabbo().getHabboInventory().getMarketplaceItems());

        for(MarketPlaceOffer offer : offers)
        {
            if(offer.getState().equals(MarketPlaceState.SOLD))
            {
                client.getHabbo().getHabboInventory().removeMarketplaceOffer(offer);
                credits += (offer.getPrice() - (int)(Math.ceil(offer.getPrice() / 100.0)));
                removeUser(offer);
                offer.needsUpdate(true);
                Emulator.getThreading().run(offer);
            }
        }

        offers.clear();
        client.getHabbo().getHabboInfo().addCredits(credits);
        client.sendResponse(new UserCreditsComposer(client.getHabbo()));
    }

    private static void removeUser(MarketPlaceOffer offer)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE marketplace_items SET user_id = ? WHERE id = ?");
            statement.setInt(1, -1);
            statement.setInt(2, offer.getOfferId());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    /**
     * Calculates the commission that is being added for the given price.
     * @param price The price to calculate the commission for.
     * @return The new price including the commission.
     */
    public static int calculateCommision(int price)
    {
        return price + (int)Math.ceil(price / 100.0);
    }
}
