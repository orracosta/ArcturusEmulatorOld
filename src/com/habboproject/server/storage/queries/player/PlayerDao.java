package com.habboproject.server.storage.queries.player;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.players.data.PlayerAvatarData;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.players.types.PlayerSettings;
import com.habboproject.server.game.players.types.PlayerStatistics;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class PlayerDao {
    public static Player getPlayer(String ssoTicket) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT p.id as playerId, p.username AS playerData_username," +
                    " p.figure AS playerData_figure, p.motto AS playerData_motto, p.credits AS playerData_credits," +
                    " p.vip_points AS playerData_vipPoints, p.rank AS playerData_rank, p.last_ip AS playerData_lastIp," +
                    " p.vip AS playerData_vip, p.gender AS playerData_gender, p.last_online AS playerData_lastOnline," +
                    " p.reg_timestamp AS playerData_regTimestamp, p.reg_date AS playerData_regDate, p.favourite_group" +
                    " AS playerData_favouriteGroup, p.achievement_points AS playerData_achievementPoints, p.email AS" +
                    " playerData_email, p.activity_points AS playerData_activityPoints, p.quest_id AS" +
                    " playerData_questId, p.forum_posts AS playerData_forumPosts, p.newbie_step AS playerData_newbieStep," +
                    " pSettings.volume AS playerSettings_volume, pSettings.home_room" +
                    " AS playerSettings_homeRoom, pSettings.hide_online AS playerSettings_hideOnline," +
                    " pSettings.hide_inroom AS playerSettings_hideInRoom, pSettings.ignore_invites AS" +
                    " playerSettings_ignoreInvites, pSettings.allow_friend_requests AS" +
                    " playerSettings_allowFriendRequests, pSettings.allow_trade AS playerSettings_allowTrade," +
                    " pSettings.wardrobe AS playerSettings_wardrobe, pSettings.playlist AS playerSettings_playlist," +
                    " pSettings.navigator AS playerSettings_navigator, pSettings.chat_oldstyle AS" +
                    " playerSettings_useOldChat, pSettings.camera_follow AS playerSettings_cameraFollow," +
                    " pStats.achievement_score AS playerStats_achievementPoints, pStats.daily_respects AS" +
                    " playerStats_dailyRespects, pStats.last_totem_effect AS playerStats_lastTotemEffect," +
                    " pStats.total_respect_points AS playerStats_totalRespectPoints, pStats.help_tickets AS" +
                    " playerStats_helpTickets, pStats.help_tickets_abusive AS playerStats_helpTicketsAbusive," +
                    " pStats.cautions AS playerStats_cautions, pStats.bans AS playerStats_bans, pStats.daily_scratches" +
                    " AS playerStats_scratches FROM players p JOIN player_settings pSettings ON" +
                    " pSettings.player_id = p.id JOIN player_stats pStats ON pStats.player_id = p.id" +
                    " WHERE p.auth_ticket = ?", sqlConnection);

            preparedStatement.setString(1, ssoTicket);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new Player(resultSet, false);
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

    public static Player getPlayerFallback(String ssoTicket) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT p.id as playerId, p.username AS playerData_username," +
                    " p.figure AS playerData_figure, p.motto AS playerData_motto, p.credits AS playerData_credits," +
                    " p.vip_points AS playerData_vipPoints, p.rank AS playerData_rank, p.vip AS playerData_vip," +
                    " p.gender AS playerData_gender, p.last_online AS playerData_lastOnline," +
                    " p.reg_timestamp AS playerData_regTimestamp, p.reg_date AS playerData_regDate," +
                    " p.favourite_group AS playerData_favouriteGroup,p.achievement_points AS playerData_achievementPoints," +
                    " p.email AS playerData_email,p.activity_points AS playerData_activityPoints," +
                    " p.quest_id AS playerData_questId,p.last_ip AS playerData_lastIp," +
                    " p.forum_posts AS playerData_forumPosts, p.newbie_step AS playerData_newbieStep" +
                    " FROM players p" +
                    " WHERE p.auth_ticket = ?", sqlConnection);

            preparedStatement.setString(1, ssoTicket);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new Player(resultSet, true);
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

    public static PlayerData getDataByUsername(String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT id, motto, figure, gender, email, rank, credits, vip_points, activity_points, reg_date, last_online, vip, achievement_points, reg_timestamp, favourite_group, last_ip, quest_id, forum_posts, newbie_step FROM players WHERE username = ?", sqlConnection);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new PlayerData(resultSet.getInt("id"), username, resultSet.getString("motto"), resultSet.getString("figure"), resultSet.getString("gender"), resultSet.getString("email") == null ? "" : resultSet.getString("email"), resultSet.getInt("rank"), resultSet.getInt("credits"), resultSet.getInt("vip_points"), resultSet.getInt("activity_points"), resultSet.getString("reg_date"), resultSet.getInt("last_online"), resultSet.getString("vip").equals("1"), resultSet.getInt("achievement_points"), resultSet.getInt("reg_timestamp"), resultSet.getInt("favourite_group"), resultSet.getString("last_ip"), resultSet.getInt("quest_id"), resultSet.getInt("forum_posts"), resultSet.getString("newbie_step"));
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

    public static PlayerData getDataById(int id) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT id, username, motto, figure, gender, email, rank, credits, vip_points, activity_points, reg_date, last_online, vip, achievement_points, reg_timestamp, favourite_group, last_ip, quest_id, forum_posts, newbie_step FROM players WHERE id = ?", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new PlayerData(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("motto"), resultSet.getString("figure"), resultSet.getString("gender"), resultSet.getString("email") == null ? "" : resultSet.getString("email"), resultSet.getInt("rank"), resultSet.getInt("credits"), resultSet.getInt("vip_points"), resultSet.getInt("activity_points"), resultSet.getString("reg_date"), resultSet.getInt("last_online"), resultSet.getString("vip").equals("1"), resultSet.getInt("achievement_points"), resultSet.getInt("reg_timestamp"), resultSet.getInt("favourite_group"), resultSet.getString("last_ip"), resultSet.getInt("quest_id"), resultSet.getInt("forum_posts"), resultSet.getString("newbie_step"));
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

    public static PlayerAvatar getAvatarById(int id, byte mode) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            String query;

            switch (mode) {
                case PlayerAvatar.USERNAME_FIGURE:
                    query = "SELECT username, figure, forum_posts FROM players WHERE id = ?";
                    break;


                default:
                case PlayerAvatar.USERNAME_FIGURE_MOTTO:
                    query = "SELECT username, figure, motto, forum_posts FROM players WHERE id = ?";
                    break;
            }

            preparedStatement = SqlHelper.prepare(query, sqlConnection);

            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final PlayerAvatar playerAvatar = new PlayerAvatarData(id, resultSet.getString("username"), resultSet.getString("figure"),
                        resultSet.getInt("forum_posts"));

                if (mode == PlayerAvatar.USERNAME_FIGURE_MOTTO) {
                    playerAvatar.setMotto(resultSet.getString("motto"));
                }

                return playerAvatar;
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

    public static int getForumPostsByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT forum_posts FROM players WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, playerId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("forum_posts");
            }
        }
        catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        }
        finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
        return 0;
    }

    public static String getMottoByPlayerId(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            String query = "SELECT motto FROM players WHERE id = ?";

            preparedStatement = SqlHelper.prepare(query, sqlConnection);

            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("motto");
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return "";
    }

    public static PlayerSettings getSettingsById(int id) {
        // TODO: Cache, cache, cache!

        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM player_settings WHERE player_id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new PlayerSettings(resultSet, false);
            } else {
                // close old statement
                SqlHelper.closeSilently(preparedStatement);

                preparedStatement = SqlHelper.prepare("INSERT into player_settings (`player_id`) VALUES(?)", sqlConnection);
                preparedStatement.setInt(1, id);

                SqlHelper.executeStatementSilently(preparedStatement, false);

                return new PlayerSettings();
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return new PlayerSettings();
    }

    public static PlayerStatistics getStatisticsById(int id) {
        Connection sqlConnection = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM player_stats WHERE player_id = ? LIMIT 1;", sqlConnection);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new PlayerStatistics(resultSet, false);
            } else {
                SqlHelper.closeSilently(preparedStatement);

                preparedStatement = SqlHelper.prepare("INSERT into player_stats (`player_id`) VALUES(?)", sqlConnection);
                preparedStatement.setInt(1, id);

                SqlHelper.executeStatementSilently(preparedStatement, false);

                return new PlayerStatistics(id);
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return new PlayerStatistics(id);
    }

    public static void updatePlayerStatus(Player player, boolean online, boolean setLastOnline) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET online = ?" + (setLastOnline ? ", last_online = ?, last_ip = ?" : "") + " WHERE id = ?", sqlConnection);
            preparedStatement.setString(1, online ? "1" : "0");

            if (setLastOnline) {
                preparedStatement.setLong(2, Comet.getTime());
                preparedStatement.setString(3, player.getData().getIpAddress());
                preparedStatement.setInt(4, player.getId());
            } else {
                preparedStatement.setInt(2, player.getId());
            }

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static String getUsernameByPlayerId(int playerId) {
        // TODO: Cache, cache cache!

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `username` FROM players WHERE `id` = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

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

        return null;
    }

    public static int getIdByUsername(String username) {
        if (PlayerManager.getInstance().getPlayerIdByUsername(username) != -1)
            return PlayerManager.getInstance().getPlayerIdByUsername(username);

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `id` FROM players WHERE `username` = ?", sqlConnection);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getInt("id");
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

    public static String getIpAddress(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `last_ip` FROM players WHERE `id` = ?", sqlConnection);
            preparedStatement.setInt(1, playerId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("last_ip");
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return "";
    }

    public static boolean updatePlayerStatistics(PlayerStatistics playerStatistics) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_stats SET achievement_score = ?, total_respect_points = ?, daily_respects = ?, help_tickets = ?, help_tickets_abusive = ?, cautions = ?, bans = ?, daily_scratches = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, playerStatistics.getAchievementPoints());
            preparedStatement.setInt(2, playerStatistics.getRespectPoints());
            preparedStatement.setInt(3, playerStatistics.getDailyRespects());
            preparedStatement.setInt(4, playerStatistics.getHelpTickets());
            preparedStatement.setInt(5, playerStatistics.getAbusiveHelpTickets());
            preparedStatement.setInt(6, playerStatistics.getCautions());
            preparedStatement.setInt(7, playerStatistics.getBans());
            preparedStatement.setInt(8, playerStatistics.getScratches());

            preparedStatement.setInt(9, playerStatistics.getPlayerId());

            return preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return false;
    }

    public static void updateHomeRoom(int homeRoom, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET home_room = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, homeRoom);
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveWardrobe(String wardrobeData, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET wardrobe = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, wardrobeData);
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveVolume(String volumeData, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET volume = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, volumeData);
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveChatStyle(boolean useOldChat, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET chat_oldstyle = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, useOldChat ? "1" : "0");
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveIgnoreInvitations(boolean ignoreInvitations, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET ignore_invites = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, ignoreInvitations ? "1" : "0");
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveAllowFriendRequests(boolean allowFriendRequests, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET allow_friend_requests = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, allowFriendRequests ? "1" : "0");
            preparedStatement.setInt(2, userId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static boolean usernameIsAvailable(String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT `id` FROM players WHERE username = ? LIMIT 1", sqlConnection);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return false;
    }

    public static void resetHomeRoom(int roomId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET home_room = 0 WHERE player_id = ?", sqlConnection);
            preparedStatement.setInt(1, roomId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void resetOnlineStatus() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET online = '0' WHERE online = '1'", sqlConnection);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void nullifyAuthTicket(int playerId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE players SET auth_ticket = NULL WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, playerId);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }


    public static void saveBatch(Map<Integer, PlayerData> playerData) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        for(PlayerData playerDataInstance : playerData.values()) {
            try {
                sqlConnection = SqlHelper.getConnection();

                preparedStatement = SqlHelper.prepare("UPDATE players SET username = ?, motto = ?, figure = ?, credits = ?, vip_points = ?, gender = ?, favourite_group = ?, activity_points = ?, quest_id = ?, achievement_points = ?, forum_posts = ? WHERE id = ?", sqlConnection);

                preparedStatement.setString(1, playerDataInstance.getUsername());
                preparedStatement.setString(2, playerDataInstance.getMotto());
                preparedStatement.setString(3, playerDataInstance.getFigure());
                preparedStatement.setInt(4, playerDataInstance.getCredits());
                preparedStatement.setInt(5, playerDataInstance.getVipPoints());
                preparedStatement.setString(6, playerDataInstance.getGender());
                preparedStatement.setInt(7, playerDataInstance.getFavouriteGroup());
                preparedStatement.setInt(8, playerDataInstance.getActivityPoints());
                preparedStatement.setInt(9, playerDataInstance.getQuestId());
                preparedStatement.setInt(10, playerDataInstance.getAchievementPoints());
                preparedStatement.setInt(11, playerDataInstance.getForumPosts());
                preparedStatement.setInt(12, playerDataInstance.getId());

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                SqlHelper.handleSqlException(e);
            } finally {
                SqlHelper.closeSilently(preparedStatement);
                SqlHelper.closeSilently(sqlConnection);
            }
        }
    }

    public static void dailyPlayerUpdate(int dailyRespects, int dailyScratches) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_stats SET daily_respects = ?, daily_scratches = ?;", sqlConnection);

            preparedStatement.setInt(1, dailyRespects);
            preparedStatement.setInt(2, dailyScratches);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveNavigatorSize(String navigatorData, int userId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE player_settings SET navigator = ? WHERE player_id = ?", sqlConnection);
            preparedStatement.setString(1, navigatorData);
            preparedStatement.setInt(2, userId);
            SqlHelper.executeStatementSilently(preparedStatement, false);

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updateEventPoints(String username) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        String query = "UPDATE players SET ";

        if (CometSettings.defaultEventPointsColumn.contains(";")) {
            int length = CometSettings.defaultEventPointsColumn.split("[;]").length;
            int currentIndex = 0;

            for (String column : CometSettings.defaultEventPointsColumn.split("[;]")) {
                query = query + column + " = " + column + " + " + CometSettings.defaultEventPointsQuantity;
                if (++currentIndex != length) {
                    query = query + ", ";
                }
            }
        } else {
            query = String.valueOf(query) + CometSettings.defaultEventPointsColumn + " = " + CometSettings.defaultEventPointsColumn + " + " + CometSettings.defaultEventPointsQuantity;
        }

        query = String.valueOf(query) + " WHERE username = ? LIMIT 1";

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare(query, sqlConnection);
            preparedStatement.setString(1, username);

            SqlHelper.executeStatementSilently(preparedStatement, false);
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
