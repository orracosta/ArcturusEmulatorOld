package com.habboproject.server.storage.queries.rooms;

import com.habboproject.server.api.game.rooms.settings.*;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.rooms.models.CustomModel;
import com.habboproject.server.game.rooms.models.types.StaticRoomModel;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.game.rooms.types.RoomPromotion;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.JsonFactory;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomDao {
    public static Map<String, StaticRoomModel> getModels() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<String, StaticRoomModel> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM room_models", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.put(resultSet.getString("id"), new StaticRoomModel(resultSet));
            }

        } catch (Exception e) {
            if (e instanceof SQLException)
                SqlHelper.handleSqlException(((SQLException) e));
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return data;
    }

    public static RoomData getRoomDataById(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new RoomData(resultSet);
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

    public static Map<Integer, RoomData> getRoomsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, RoomData> rooms = new ListOrderedMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE owner_id = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                rooms.put(resultSet.getInt("id"), new RoomData(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return rooms;
    }

    public static List<RoomData> getRoomsByQuery(String query) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomData> rooms = Lists.newArrayList();
        List<Integer> groups = Lists.newArrayList();

        try {
            sqlConnection = SqlHelper.getConnection();

            if (query.startsWith("owner:")) {
                preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE owner = ?", sqlConnection);

                preparedStatement.setString(1, query.split("owner:")[1]);
            } else if (query.startsWith("tag:")) {
                preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE tags LIKE ? LIMIT 50", sqlConnection);

                String tagName = SqlHelper.escapeWildcards(query.split("tag:")[1]);

                preparedStatement.setString(1, "%" + tagName + "%");
            } else if (query.startsWith("group:")) {
                preparedStatement = SqlHelper.prepare("SELECT * FROM groups WHERE name LIKE ? LIMIT 50", sqlConnection);

                String groupName = SqlHelper.escapeWildcards(query.split("group:")[1]);

                preparedStatement.setString(1, "%" + groupName + "%");
            } else {
                // escape wildcard characters
                query = SqlHelper.escapeWildcards(query);

                preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE name LIKE ? LIMIT 50", sqlConnection);
                preparedStatement.setString(1, query + "%");
            }

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (query.startsWith("group:")) {
                    groups.add(resultSet.getInt("id"));
                } else {
                    rooms.add(new RoomData(resultSet));
                }
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        if (query.startsWith("group:")) {
            for (Integer groupId : groups) {
                try {
                    sqlConnection = SqlHelper.getConnection();

                    preparedStatement = SqlHelper.prepare("SELECT * FROM rooms WHERE group_id = ?", sqlConnection);
                    preparedStatement.setInt(1, groupId);
                    resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        rooms.add(new RoomData(resultSet));
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

        return rooms;
    }

    public static int createRoom(String name, CustomModel model, String description, int category, int maxVisitors, RoomTradeState tradeState, int userId, String username, int wallThickness, int floorThickness, String decorations, boolean hideWalls) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT into rooms (`owner_id`, `owner`, `name`, `heightmap`, `description`, `category`, `max_users`, `trade_state`, `thickness_wall`, `thickness_floor`, `decorations`, `hide_walls`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, JsonFactory.getInstance().toJson(model));
            preparedStatement.setString(5, description);
            preparedStatement.setInt(6, category);
            preparedStatement.setInt(7, maxVisitors);
            preparedStatement.setString(8, tradeState.toString());
            preparedStatement.setInt(9, wallThickness);
            preparedStatement.setInt(10, floorThickness);
            preparedStatement.setString(11, decorations);
            preparedStatement.setString(12, hideWalls ? "1" : "0");

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return resultSet.getInt(1);
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

    public static int createRoom(String name, String model, String description, int category, int maxVisitors, RoomTradeState tradeState, int userId, String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("INSERT into rooms (`owner_id`, `owner`, `name`, `model`, `description`, `category`, `max_users`, `trade_state`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, model);
            preparedStatement.setString(5, description);
            preparedStatement.setInt(6, category);
            preparedStatement.setInt(7, maxVisitors);
            preparedStatement.setString(8, tradeState.toString());

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return resultSet.getInt(1);
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

    public static void updateRoom(int roomId, String name, String description, int ownerId, String owner, int category, int maxUsers, RoomAccessType access,
                                  String password, int score, String tags, String decor, String model, boolean hideWalls, int thicknessWall,
                                  int thicknessFloor, boolean allowWalkthrough, boolean allowPets, String heightmap, RoomTradeState tradeState, RoomMuteState whoCanMute,
                                  RoomKickState whoCanKick, RoomBanState whoCanBan, int bubbleMode, int bubbleType, int bubbleScroll,
                                  int chatDistance, int antiFloodSettings, String disabledCommands, int groupId, String requiredBadge) {

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("UPDATE rooms SET name = ?, description = ?, owner_id = ?, owner = ?, category = ?," +
                            " max_users = ?, access_type = ?, password = ?, score = ?, tags = ?, decorations = ?, model = ?, hide_walls = ?, thickness_wall = ?," +
                            " thickness_floor = ?, allow_walkthrough = ?, allow_pets = ?, heightmap = ?, mute_state = ?, ban_state = ?, kick_state = ?," +
                            "bubble_mode = ?, bubble_type = ?, bubble_scroll = ?, chat_distance = ?, flood_level = ?, trade_state = ?, disabled_commands = ?, group_id = ?, required_badge = ? WHERE id = ?",
                    sqlConnection);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, ownerId);
            preparedStatement.setString(4, owner);
            preparedStatement.setInt(5, category);
            preparedStatement.setInt(6, maxUsers);
            preparedStatement.setString(7, access.toString().toLowerCase());
            preparedStatement.setString(8, password);
            preparedStatement.setInt(9, score);
            preparedStatement.setString(10, tags);
            preparedStatement.setString(11, decor);
            preparedStatement.setString(12, model);
            preparedStatement.setString(13, hideWalls ? "1" : "0");
            preparedStatement.setInt(14, thicknessWall);
            preparedStatement.setInt(15, thicknessFloor);
            preparedStatement.setString(16, allowWalkthrough ? "1" : "0");
            preparedStatement.setString(17, allowPets ? "1" : "0");
            preparedStatement.setString(18, heightmap);
            preparedStatement.setString(19, whoCanMute.toString());
            preparedStatement.setString(20, whoCanBan.toString());
            preparedStatement.setString(21, whoCanKick.toString());
            preparedStatement.setInt(22, bubbleMode);
            preparedStatement.setInt(23, bubbleType);
            preparedStatement.setInt(24, bubbleScroll);
            preparedStatement.setInt(25, chatDistance);
            preparedStatement.setInt(26, antiFloodSettings);
            preparedStatement.setString(27, tradeState.toString());
            preparedStatement.setString(28, disabledCommands);
            preparedStatement.setInt(29, groupId);
            preparedStatement.setString(30, requiredBadge);

            preparedStatement.setInt(31, roomId);

            preparedStatement.execute();
        } catch (SQLException e) {
            Comet.getServer().getLogger().error("Failed to save room data" + (e.getMessage().contains("access_type") ? " - Access type: " + access.toString().toLowerCase() : ""));
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteRoom(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();
            preparedStatement = SqlHelper.prepare("DELETE FROM rooms WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static List<RoomData> getHighestScoredRooms() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        List<RoomData> roomData = Lists.newArrayList();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM rooms ORDER by score DESC LIMIT 50", sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomData.add(new RoomData(resultSet));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return roomData;
    }

    public static void getActivePromotions(Map<Integer, RoomPromotion> roomPromotions) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM rooms_promoted WHERE time_expire > " + Comet.getTime(), sqlConnection);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                roomPromotions.put(resultSet.getInt("room_id"), new RoomPromotion(resultSet.getInt("room_id"), resultSet.getString("name"), resultSet.getString("description"), resultSet.getLong("time_start"), resultSet.getLong("time_expire")));
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updatePromotedRoom(RoomPromotion roomPromotion) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE rooms_promoted SET name = ?, description = ?, time_expire = ? WHERE room_id = ?", sqlConnection);

            preparedStatement.setString(1, roomPromotion.getPromotionName());
            preparedStatement.setString(2, roomPromotion.getPromotionDescription());

            preparedStatement.setLong(3, roomPromotion.getTimestampFinish());
            preparedStatement.setInt(4, roomPromotion.getRoomId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void createPromotedRoom(RoomPromotion roomPromotion) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into rooms_promoted (room_id, name, description, time_start, time_expire) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), " +
                    "description = VALUES(description), time_start = VALUES(time_start), time_expire = VALUES(time_expire);", sqlConnection);

            preparedStatement.setInt(1, roomPromotion.getRoomId());
            preparedStatement.setString(2, roomPromotion.getPromotionName());
            preparedStatement.setString(3, roomPromotion.getPromotionDescription());
            preparedStatement.setLong(4, roomPromotion.getTimestampStart());
            preparedStatement.setLong(5, roomPromotion.getTimestampFinish());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void deleteExpiredRoomPromotions() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("DELETE FROM rooms_promoted WHERE time_expire < " + Comet.getTime(), sqlConnection);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static String getRoomOwnerNameById(int ownerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT username FROM players WHERE id = ? LIMIT 1", sqlConnection);
            preparedStatement.setInt(1, ownerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return "Unknow Player";
    }
}
