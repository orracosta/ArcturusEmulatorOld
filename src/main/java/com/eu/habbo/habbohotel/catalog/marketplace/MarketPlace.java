package com.eu.habbo.habbohotel.catalog.marketplace;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.catalog.marketplace.RequestOffersEvent;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceBuyErrorComposer;
import com.eu.habbo.messages.outgoing.catalog.marketplace.MarketplaceCancelSaleComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveHabboItemComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceItemCancelledEvent;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceItemOfferedEvent;
import com.eu.habbo.plugin.events.marketplace.MarketPlaceItemSoldEvent;
import gnu.trove.set.hash.THashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Note: A lot of data in here is not cached in the emulator but rather loaded from the database.
 * If you have a big hotel and the marketplace is used frequently this may cause some lagg.
 * If you have issues with that please let us know so we can see if we need to modify the Marketplace so
 * it caches the data in the Emulator.
 */
public class MarketPlace
{
    //Configuration. Loaded from database & updated accordingly.
    public static boolean MARKETPLACE_ENABLED = true;

    //Currency to use.
    public static int MARKETPLACE_CURRENCY = 0;

    /**
     * @param habbo The Habbo to lookup items for.
     * @return The items that are put on marketplace by the given Habbo.
     */
    public static THashSet<MarketPlaceOffer> getOwnOffers(Habbo habbo)
    {
        THashSet<MarketPlaceOffer> offers = new THashSet<MarketPlaceOffer>();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT items_base.type AS type, items.item_id AS base_item_id, items.limited_data AS ltd_data, marketplace_items.* FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE marketplace_items.user_id = ?"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    offers.add(new MarketPlaceOffer(set, true));
                }
            }
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
        MarketPlaceOffer offer = habbo.getInventory().getOffer(offerId);

        if (!Emulator.getPluginManager().fireEvent(new MarketPlaceItemCancelledEvent(offer)).isCancelled())
        {
            takeBackItem(habbo, offer);
        }
    }

    /**
     * Removes an offer from the marketplace.
     * @param habbo The Habbo that owns the item.
     * @param offer The offer.
     */
    private static void takeBackItem(Habbo habbo, MarketPlaceOffer offer)
    {
        if(offer != null && habbo.getInventory().getMarketplaceItems().contains(offer))
        {
            RequestOffersEvent.cachedResults.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
            {
                try (PreparedStatement ownerCheck = connection.prepareStatement("SELECT user_id FROM marketplace_items WHERE id = ?"))
                {
                    ownerCheck.setInt(1, offer.getOfferId());
                    try (ResultSet ownerSet = ownerCheck.executeQuery())
                    {
                        ownerSet.last();

                        if (ownerSet.getRow() == 0)
                        {
                            return;
                        }

                        habbo.getInventory().removeMarketplaceOffer(offer);

                        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM marketplace_items WHERE id = ? AND state != 2"))
                        {
                            statement.setInt(1, offer.getOfferId());
                            int count = statement.executeUpdate();

                            if (count != 0)
                            {
                                try (PreparedStatement updateItems = connection.prepareStatement("UPDATE items SET user_id = ? WHERE id = ? LIMIT 1"))
                                {
                                    updateItems.setInt(1, habbo.getHabboInfo().getId());
                                    updateItems.setInt(2, offer.getSoldItemId());
                                    updateItems.execute();

                                    try (PreparedStatement selectItem = connection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1"))
                                    {
                                        selectItem.setInt(1, offer.getSoldItemId());
                                        try (ResultSet set = selectItem.executeQuery())
                                        {
                                            while (set.next())
                                            {
                                                HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(set);
                                                habbo.getInventory().getItemsComponent().addItem(item);
                                                habbo.getClient().sendResponse(new MarketplaceCancelSaleComposer(offer, true));
                                                habbo.getClient().sendResponse(new AddHabboItemComposer(item));
                                                habbo.getClient().sendResponse(new InventoryRefreshComposer());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
                habbo.getClient().sendResponse(new MarketplaceCancelSaleComposer(offer, false));
            }
        }
    }

    /**
     * Searches the marketplace for specific items in the given price range.
     * @param minPrice The minimum price of the offers.
     * @param maxPrice The maximum price of the offers
     * @param search The search criteria for the offers.
     * @param sort The sort criteria for the offers.
     * @return The result of the search.
     */
    public static List<MarketPlaceOffer> getOffers(int minPrice, int maxPrice, String search, int sort)
    {
        List<MarketPlaceOffer> offers = new ArrayList<>(10);

        //String query = "SELECT items_base.type AS type, items.item_id AS base_item_id, items.limited_data AS ltd_data, marketplace_items.*, COUNT(*) as number, AVG(price) as avg, MIN(price) as minPrice FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = ? AND timestamp >= ?";
        /*String query = "SELECT " +
                            "B.* FROM marketplace_items a " +
                        "INNER JOIN (SELECT " +
                                "items_base.type AS type, " +
                                "items.item_id AS base_item_id, " +
                                "items.limited_data AS ltd_data, " +
                                "marketplace_items.*, " +
                                "AVG(price) as avg, " +
                                "MIN(marketplace_items.price) as minPrice, " +
                                "MAX(marketplace_items.price) as maxPrice, " +
                                "COUNT(*) as number " +
                            "FROM marketplace_items " +
                            "INNER JOIN items ON marketplace_items.item_id = items.id " +
                            "INNER JOIN items_base ON items.item_id = items_base.id " +
                            "WHERE state = 1 AND timestamp > ?";*/

        String query =  "SELECT " +
                            "B.* FROM marketplace_items a " +
                        "INNER JOIN (" +
                                        "SELECT " +
                                        "items.item_id AS base_item_id, " +
                                        "items.limited_data AS ltd_data, " +
                                        "marketplace_items.*, " +
                                        "AVG(price) as avg, " +
                                        "MIN(marketplace_items.price) as minPrice, " +
                                        "MAX(marketplace_items.price) as maxPrice, " +
                                        "COUNT(*) as number, " +
                                        //"SUM(DATE(from_unixtime(timestamp)) >= (NOW() - INTERVAL 1 DAY)) as offer_count_today, " +
                                        "(SELECT COUNT(*) FROM marketplace_items c INNER JOIN items as items_b ON c.item_id = items_b.id WHERE state = 2 AND items_b.item_id = base_item_id AND DATE(from_unixtime(sold_timestamp)) = CURDATE()) as sold_count_today " +
                                    "FROM marketplace_items " +
                                    "INNER JOIN items ON marketplace_items.item_id = items.id " +
                                    "WHERE state = 1 AND timestamp > ?";
        if(minPrice > 0)
        {
            query += " AND CEIL(price + (price / 100)) >= " + minPrice;
        }
        if(maxPrice > 0 && maxPrice > minPrice)
        {
            query += " AND CEIL(price + (price / 100)) <= " + maxPrice;
        }
        if(search.length() > 0)
        {
            query += " AND items_base.public_name LIKE ?";
        }

        query += " GROUP BY base_item_id, ltd_data";

        switch (sort)
        {
            case 6:
                query += " ORDER BY number ASC";
                break;
            case 5:
                query += " ORDER BY number DESC";
                break;
            case 4:
                query += " ORDER BY sold_count_today ASC";
                break;
            case 3:
                query += " ORDER BY sold_count_today DESC";
                break;
            case 2:
                query += " ORDER BY minPrice ASC";
                break;
            default:
            case 1:
                query += " ORDER BY minPrice DESC";
                break;
        }
        if (sort == 3)
        {

        }
        if (sort == 2)
        {
        }
        else
        {
        }

        query += ")";

        query += " AS B ON a.id = B.id";

        query += " LIMIT 100";

        System.out.println(query);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setInt(1, Emulator.getIntUnixTimestamp() - 172800);
            if(search.length() > 0)
                statement.setString(3, "%"+search+"%");

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    offers.add(new MarketPlaceOffer(set, false));
                }
            }
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
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT avg(price) as price, COUNT(*) as sold, (datediff(NOW(), DATE(from_unixtime(timestamp)))) as day FROM marketplace_items INNER JOIN items ON items.id = marketplace_items.item_id INNER JOIN items_base ON items.item_id = items_base.id WHERE items.limited_data = '0:0' AND state = 2 AND items_base.sprite_id = ? AND DATE(from_unixtime(timestamp)) >= NOW() - INTERVAL 30 DAY GROUP BY DATE(from_unixtime(timestamp))"))
        {
            statement.setInt(1, itemId);

            message.appendInt(avarageLastXDays(itemId, 7));
            message.appendInt(itemsOnSale(itemId));
            message.appendInt(30);

            try (ResultSet set = statement.executeQuery())
            {
                set.last();
                message.appendInt(set.getRow());
                set.beforeFirst();

                while (set.next())
                {
                    message.appendInt(-set.getInt("day"));
                    message.appendInt(MarketPlace.calculateCommision(set.getInt("price")));
                    message.appendInt(set.getInt("sold"));
                }
            }

            message.appendInt(1);
            message.appendInt(itemId);
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
        int number = 0;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) as number, AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = 1 AND timestamp >= ? AND items_base.sprite_id = ?"))
        {
            statement.setInt(1, Emulator.getIntUnixTimestamp() - 172800);
            statement.setInt(2, baseItemId);
            try (ResultSet set = statement.executeQuery())
            {
                set.first();
                number = set.getInt("number");
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return number;
    }

    /**
     * @param baseItemId The base item id that should be looked up. (items_base table)
     * @param days The amount of days.
     * @return The average amount of coins this item was over the given amount of days.
     */
    private static int avarageLastXDays(int baseItemId, int days)
    {
        int avg = 0;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT AVG(price) as avg FROM marketplace_items INNER JOIN items ON marketplace_items.item_id = items.id INNER JOIN items_base ON items.item_id = items_base.id WHERE state = 2 AND DATE(from_unixtime(timestamp)) >= NOW() - INTERVAL ? DAY AND items_base.sprite_id = ?"))
        {
            statement.setInt(1, days);
            statement.setInt(2, baseItemId);

            try (ResultSet set = statement.executeQuery())
            {
                set.first();
                avg = set.getInt("avg");
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return calculateCommision(avg);
    }

    /**
     * Buys an item from the marketplace.
     * @param offerId The offer id that should be bought.
     * @param client The GameClient that buys it.
     */
    public static void buyItem(int offerId, GameClient client)
    {
        RequestOffersEvent.cachedResults.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM marketplace_items WHERE id = ? LIMIT 1"))
            {
                statement.setInt(1, offerId);
                try (ResultSet set = statement.executeQuery())
                {
                    if (set.next())
                    {
                        try (PreparedStatement itemStatement = connection.prepareStatement("SELECT * FROM items WHERE id = ? LIMIT 1"))
                        {
                            itemStatement.setInt(1, set.getInt("item_id"));
                            try (ResultSet itemSet = itemStatement.executeQuery())
                            {
                                itemSet.first();

                                if (itemSet.getRow() > 0)
                                {
                                    int price = MarketPlace.calculateCommision(set.getInt("price"));
                                    if (set.getInt("state") != 1)
                                    {
                                        sendErrorMessage(client, set.getInt("item_id"), offerId);
                                    }
                                    else if ((MARKETPLACE_CURRENCY == 0 && price > client.getHabbo().getHabboInfo().getCredits()) || (MARKETPLACE_CURRENCY > 0 && price > client.getHabbo().getHabboInfo().getCurrencyAmount(MARKETPLACE_CURRENCY)))
                                    {
                                        client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.NOT_ENOUGH_CREDITS, 0, offerId, price));
                                    }
                                    else
                                    {
                                        try (PreparedStatement updateOffer = connection.prepareStatement("UPDATE marketplace_items SET state = 2, sold_timestamp = ? WHERE id = ?"))
                                        {
                                            updateOffer.setInt(1, Emulator.getIntUnixTimestamp());
                                            updateOffer.setInt(2, offerId);
                                            updateOffer.execute();
                                        }
                                        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(set.getInt("user_id"));
                                        HabboItem item = Emulator.getGameEnvironment().getItemManager().loadHabboItem(itemSet);

                                        MarketPlaceItemSoldEvent event = new MarketPlaceItemSoldEvent(habbo, client.getHabbo(), item, set.getInt("price"));
                                        if (Emulator.getPluginManager().fireEvent(event).isCancelled())
                                        {
                                            return;
                                        }
                                        event.price = calculateCommision(event.price);

                                        item.setUserId(client.getHabbo().getHabboInfo().getId());
                                        item.needsUpdate(true);
                                        Emulator.getThreading().run(item);

                                        client.getHabbo().getInventory().getItemsComponent().addItem(item);

                                        if (MARKETPLACE_CURRENCY == 0)
                                        {
                                            client.getHabbo().getHabboInfo().addCredits(-event.price);
                                        }
                                        else
                                        {
                                            client.getHabbo().givePoints(MARKETPLACE_CURRENCY, -event.price);
                                        }

                                        client.sendResponse(new UserCreditsComposer(client.getHabbo()));
                                        client.sendResponse(new AddHabboItemComposer(item));
                                        client.sendResponse(new InventoryRefreshComposer());
                                        client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.REFRESH, 0, offerId, price));

                                        if (habbo != null)
                                        {
                                            habbo.getInventory().getOffer(offerId).setState(MarketPlaceState.SOLD);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT marketplace_items.*, COUNT( * ) AS count\n" +
                "FROM marketplace_items\n" +
                "INNER JOIN items ON marketplace_items.item_id = items.id\n" +
                "INNER JOIN items_base ON items.item_id = items_base.id\n" +
                "WHERE items_base.sprite_id = ( \n" +
                "SELECT items_base.sprite_id\n" +
                "FROM items_base\n" +
                "WHERE items_base.id = ? LIMIT 1)\n" +
                "ORDER BY price DESC\n" +
                "LIMIT 1"))
        {
            statement.setInt(1, baseItemId);
            try (ResultSet countSet = statement.executeQuery())
            {
                countSet.last();
                if (countSet.getRow() == 0)
                    client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.SOLD_OUT, 0, offerId, 0));
                else
                {
                    countSet.first();
                    client.sendResponse(new MarketplaceBuyErrorComposer(MarketplaceBuyErrorComposer.UPDATES, countSet.getInt("count"), countSet.getInt("id"), MarketPlace.calculateCommision(countSet.getInt("price"))));
                }
            }
        }
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

        if (!item.getBaseItem().allowMarketplace() || price < 0)
            return false;

        MarketPlaceItemOfferedEvent event = new MarketPlaceItemOfferedEvent(client.getHabbo(), item, price);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled())
        {
            return false;
        }

        RequestOffersEvent.cachedResults.clear();
        try
        {
            MarketPlaceOffer offer = new MarketPlaceOffer(event.item, event.price, client.getHabbo());

            if(offer != null)
            {
                client.getHabbo().getInventory().addMarketplaceOffer(offer);

                client.getHabbo().getInventory().getItemsComponent().removeHabboItem(event.item);
                client.sendResponse(new RemoveHabboItemComposer(event.item.getId()));
                client.sendResponse(new InventoryRefreshComposer());
                item.setUserId(-1);
                item.needsUpdate(true);
                Emulator.getThreading().run(item);
            }
        }
        catch (SQLException e)
        {
			Emulator.getLogging().logSQLException(e);
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
        offers.addAll(client.getHabbo().getInventory().getMarketplaceItems());

        for(MarketPlaceOffer offer : offers)
        {
            if(offer.getState().equals(MarketPlaceState.SOLD))
            {
                client.getHabbo().getInventory().removeMarketplaceOffer(offer);
                credits += offer.getPrice();
                removeUser(offer);
                offer.needsUpdate(true);
                Emulator.getThreading().run(offer);
            }
        }

        offers.clear();

        if (MARKETPLACE_CURRENCY == 0)
        {
            client.getHabbo().getHabboInfo().addCredits(credits);
        }
        else
        {
            client.getHabbo().givePoints(MARKETPLACE_CURRENCY, credits);
        }
        client.sendResponse(new UserCreditsComposer(client.getHabbo()));
    }

    private static void removeUser(MarketPlaceOffer offer)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE marketplace_items SET user_id = ? WHERE id = ?"))
        {
            statement.setInt(1, -1);
            statement.setInt(2, offer.getOfferId());
            statement.execute();
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
