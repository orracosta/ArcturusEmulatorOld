package com.habboproject.server.storage.queries.marketplace;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferItem;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by brend on 01/02/2017.
 */
public class MarketplaceDao {
    public static void loadOffers(List<MarketplaceOfferItem> offers) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            try {
                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("SELECT * FROM marketplace_offers", sqlConnection);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    offers.add(new MarketplaceOfferItem(resultSet.getInt("id"), resultSet.getInt("item_id"), resultSet.getInt("player_id"), resultSet.getInt("type"), resultSet.getInt("limited_stack"), resultSet.getInt("limited_number"), resultSet.getInt("state"), resultSet.getInt("price"), resultSet.getInt("final_price"), resultSet.getInt("time")));
                }
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
                SqlHelper.closeSilently(resultSet);
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static MarketplaceOfferItem createOffer(int playerId, PlayerItem playerItem, int price, int finalPrice, int type) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        int time = (int) Comet.getTime();
        int limitedStack = 0;
        int limitedNumber = 0;

        if (playerItem.getLimitedEditionItem() != null) {
            limitedStack = playerItem.getLimitedEditionItem().getLimitedRare();
            limitedNumber = playerItem.getLimitedEditionItem().getLimitedRareTotal();
        }

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT INTO marketplace_offers VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)", sqlConnection, (boolean) true);
            preparedStatement.setInt(1, playerItem.getDefinition().getId());
            preparedStatement.setInt(2, playerId);
            preparedStatement.setInt(3, type);
            preparedStatement.setInt(4, limitedStack);
            preparedStatement.setInt(5, limitedNumber);
            preparedStatement.setInt(6, 1);
            preparedStatement.setInt(7, price);
            preparedStatement.setInt(8, finalPrice);
            preparedStatement.setInt(9, time);
            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                MarketplaceOfferItem marketplaceOfferItem = new MarketplaceOfferItem(resultSet.getInt(1), playerItem.getDefinition().getId(), playerId, type, limitedStack, limitedNumber, 1, price, finalPrice, time);
                return marketplaceOfferItem;
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
        return null;
    }

    public static void updateOffer(MarketplaceOfferItem offer) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            try {
                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("UPDATE marketplace_offers SET state = ? WHERE id = ? LIMIT 1", sqlConnection);
                preparedStatement.setInt(1, offer.getState());
                preparedStatement.setInt(2, offer.getOfferId());
                preparedStatement.execute();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int getItemIdByOffer(int offerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("SELECT item_id FROM marketplace_offers WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, offerId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("item_id");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
        return 0;
    }

    public static void deleteOffer(MarketplaceOfferItem offer) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            try {
                sqlConnection = SqlHelper.getConnection();
                preparedStatement = SqlHelper.prepare("DELETE FROM marketplace_offers WHERE id = ? AND player_id = ? LIMIT 1", sqlConnection);
                preparedStatement.setInt(1, offer.getOfferId());
                preparedStatement.setInt(2, offer.getPlayerId());
                preparedStatement.execute();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
