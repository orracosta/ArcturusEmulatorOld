package com.habboproject.server.storage.queries.catalog;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.catalog.types.CatalogFrontPage;
import com.habboproject.server.game.catalog.types.CatalogItem;
import com.habboproject.server.game.catalog.types.CatalogPage;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CatalogDao {
    public static void getPages(Map<Integer, CatalogPage> pages) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM catalog_pages WHERE visible = '1' ORDER BY order_num", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                try {
                    int pageId = resultSet.getInt("id");
                    pages.put(pageId, new CatalogPage(resultSet, CatalogManager.getInstance().getItemsForPage(pageId)));
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Comet.getServer().getLogger().warn("Failed to load catalog page: " + resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void getItems(Map<Integer, CatalogItem> items) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM catalog_items", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                try {
                    CatalogItem catalogItem;
					if (resultSet.getInt("amount") <= 0 || !(catalogItem = new CatalogItem(resultSet)).canAdd())
						continue;
				
                    if (!catalogItem.getItemId().equals("-1") && catalogItem.getItems().size() == 0) {
                        Comet.getServer().getLogger().warn(String.format("Catalog Item with ID: %s and name: %s has invalid item data! (Data: %s)", catalogItem.getId(), catalogItem.getDisplayName(), catalogItem.getItemId()));
                        continue;
                    }

                    items.put(resultSet.getInt("id"), catalogItem);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Comet.getServer().getLogger().warn("Failed to load catalog item: " + resultSet.getString("id"));
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void getNews(Map<Integer, CatalogFrontPage> news) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM catalog_frontpage_news ORDER BY id", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                try {
                    int newsId = resultSet.getInt("id");
                    news.put(newsId, new CatalogFrontPage(newsId, resultSet.getString("caption"), resultSet.getString("image"), resultSet.getString("page_link"), resultSet.getInt("page_id")));
                    continue;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Comet.getServer().getLogger().warn("Failed to load catalog frontpage news: " + resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    private static Map<Integer, CatalogItem> getItemsByPage(int pageId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, CatalogItem> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM catalog_items WHERE page_id = ?", sqlConnection);
            preparedStatement.setInt(1, pageId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
//                try {
//                    int itemId = Integer.parseInt(resultSet.getString("item_ids"));
//
//                    if (itemId != -1 && !ItemManager.getInstance().getItemDefinitions().containsKey(itemId)) {
//                        continue;
//                    }
//                } catch (Exception e) {
//                    continue;
//                }

                try {
                    final CatalogItem catalogItem = new CatalogItem(resultSet);

                    if (!catalogItem.getItemId().equals("-1") && catalogItem.getItems().size() == 0) {
                        Comet.getServer().getLogger().warn(String.format("Catalog Item with ID: %s and name: %s has invalid item data! (Data: %s)", catalogItem.getId(), catalogItem.getDisplayName(), catalogItem.getItemId()));
                        continue;
                    }

                    data.put(resultSet.getInt("id"), catalogItem);
                } catch (Exception e) {
                    Comet.getServer().getLogger().warn("Error while loading catalog item: " + resultSet.getInt("id"));
                }
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }

    public static void updateLimitSellsForItem(int itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE catalog_items SET limited_sells = limited_sells + 1 WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void loadGiftBoxes(List<Integer> giftBoxesOld, List<Integer> giftBoxesNew) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM catalog_gift_wrapping", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("type").equals("old")) {
                    giftBoxesOld.add(resultSet.getInt("sprite_id"));
                } else {
                    giftBoxesNew.add(resultSet.getInt("sprite_id"));
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
