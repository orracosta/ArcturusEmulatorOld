package com.habboproject.server.storage.queries.rooms;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.collections.ConcurrentHashSet;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class RoomItemDao {

    private static Logger log = Logger.getLogger(RoomItemDao.class.getName());

    public static void getItems(Room room, Map<Long, RoomItemFloor> floorItems, Map<Long, RoomItemWall> wallItems) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT i.*, ltd.limited_id, ltd.limited_total FROM items i LEFT JOIN items_limited_edition ltd ON ltd.item_id = i.id WHERE i.room_id = ?", sqlConnection);
            preparedStatement.setInt(1, room.getId());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LimitedEditionItemData limitedEditionItemData = null;

                if (resultSet.getInt("limited_id") != 0) {
                    limitedEditionItemData = new LimitedEditionItemData(resultSet.getLong("id"), resultSet.getInt("limited_id"), resultSet.getInt("limited_total"));
                }

                if (ItemManager.getInstance().getDefinition(resultSet.getInt("base_item")) != null) {
                    if (ItemManager.getInstance().getDefinition(resultSet.getInt("base_item")).getType().equals("s"))
                        floorItems.put(resultSet.getLong("id"), RoomItemFactory.createFloor(resultSet.getLong("id"), resultSet.getInt("base_item"), room, resultSet.getInt("user_id"), resultSet.getInt("group_id"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getDouble("z"), resultSet.getInt("rot"), resultSet.getString("extra_data"), limitedEditionItemData));
                    else
                        wallItems.put(resultSet.getLong("id"), RoomItemFactory.createWall(resultSet.getLong("id"), resultSet.getInt("base_item"), room, resultSet.getInt("user_id"), resultSet.getString("wall_pos"), resultSet.getString("extra_data"), limitedEditionItemData));

                } else {
                    log.warn("Item (" + resultSet.getInt("id") + ") with invalid definition ID: " + resultSet.getInt("base_item"));
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

    public static void removeItemFromRoom(long itemId, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET room_id = 0, user_id = ?, x = 0, y = 0, z = 0, wall_pos = '' WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, userId);
            preparedStatement.setLong(2, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteItem(long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM items WHERE id = ?", sqlConnection);
            preparedStatement.setLong(1, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveData(long itemId, String data) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET extra_data = ? WHERE id = ? LIMIT 1", sqlConnection);

            preparedStatement.setString(1, data);
            preparedStatement.setLong(2, itemId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int getRoomIdById(long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `room_id` FROM items WHERE id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setLong(1, itemId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("room_id");
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

    public static void saveItemPosition(int x, int y, double height, int rotation, long id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setDouble(3, height);
            preparedStatement.setInt(4, rotation);
            preparedStatement.setLong(5, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void placeFloorItem(long roomId, int x, int y, double height, int rot, String data, long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ?, room_id = ?, extra_data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setDouble(3, height);
            preparedStatement.setInt(4, rot);
            preparedStatement.setLong(5, roomId);
            preparedStatement.setString(6, data);
            preparedStatement.setLong(7, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void placeWallItem(int roomId, String wallPos, String data, long itemId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET room_id = ?, wall_pos = ?, extra_data = ? WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            preparedStatement.setString(2, wallPos);
            preparedStatement.setString(3, data);
            preparedStatement.setLong(4, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void setBaseItem(long itemId, int baseId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items set base_item = ? WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, baseId);
            preparedStatement.setLong(2, itemId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void processBatch(List<RoomItem> itemsToStore) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        for (RoomItem roomItem : itemsToStore) {
            try {
                sqlConnection = SqlHelper.getConnection();

                preparedStatement = SqlHelper.prepare("UPDATE items SET extra_data = ? WHERE id = ?", sqlConnection);

                String data;

                if (roomItem instanceof RoomItemFloor) {
                    data = ((RoomItemFloor) roomItem).getDataObject();
                } else {
                    data = roomItem.getExtraData();
                }

                preparedStatement.setString(1, data);
                preparedStatement.setLong(2, roomItem.getId());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }
    }

    public static void saveFloorItems(List<RoomItem> items) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        for (RoomItem floor : items) {
            try {
                sqlConnection = SqlHelper.getConnection();

                preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ?, extra_data = ? WHERE id = ?", sqlConnection);
                preparedStatement.setInt(1, floor.getPosition().getX());
                preparedStatement.setInt(2, floor.getPosition().getY());
                preparedStatement.setDouble(3, floor.getPosition().getZ());
                preparedStatement.setInt(4, floor.getRotation());
                preparedStatement.setString(5, floor instanceof RoomItemWall ? "" : ((RoomItemFloor) floor).getDataObject());
                preparedStatement.setLong(6, floor.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }
    }

    public static void saveItem(RoomItemFloor floor) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ?, extra_data = ? WHERE id = ? LIMIT 1", sqlConnection);

            preparedStatement.setInt(1, floor.getPosition().getX());
            preparedStatement.setInt(2, floor.getPosition().getY());
            preparedStatement.setDouble(3, floor.getPosition().getZ());
            preparedStatement.setInt(4, floor.getRotation());
            preparedStatement.setString(5, floor.getDataObject());
            preparedStatement.setLong(6, floor.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveItem(long itemId, int x, int y, double z, int rotation, String extraData) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE items SET x = ?, y = ?, z = ?, rot = ?, extra_data = ? WHERE id = ? LIMIT 1", sqlConnection);

            preparedStatement.setInt(1, x);
            preparedStatement.setInt(2, y);
            preparedStatement.setDouble(3, z);
            preparedStatement.setInt(4, rotation);
            preparedStatement.setString(5, extraData);
            preparedStatement.setLong(6, itemId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveReward(long itemId, int playerId, final String rewardData) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into items_wired_rewards (item_id, player_id, reward_data) VALUES(?, ?, ?);", sqlConnection);

            preparedStatement.setLong(1, itemId);
            preparedStatement.setInt(2, playerId);
            preparedStatement.setString(3, rewardData);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static Map<Integer, Set<String>> getGivenRewards(long id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Set<String>> data = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT player_id, reward_data FROM items_wired_rewards WHERE item_id = ?;", sqlConnection);

            preparedStatement.setLong(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final int playerId = resultSet.getInt("player_id");
                final String rewardData = resultSet.getString("reward_data");

                if(!data.containsKey(playerId)) {
                    data.put(playerId, new HashSet<>());
                }

                data.get(playerId).add(rewardData);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }
}
