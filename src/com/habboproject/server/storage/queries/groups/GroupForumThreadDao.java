package com.habboproject.server.storage.queries.groups;

import com.google.common.collect.Maps;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThread;
import com.habboproject.server.game.groups.types.components.forum.threads.ForumThreadReply;
import com.habboproject.server.storage.SqlHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GroupForumThreadDao {
    public static Map<Integer, ForumThread> getAllMessagesForGroup(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, ForumThread> threads = new ConcurrentHashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM group_forum_messages WHERE group_id = ? ORDER BY FIELD(type, 'THREAD', 'REPLY'), pinned DESC, author_timestamp DESC;", sqlConnection);
            preparedStatement.setInt(1, groupId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                switch (resultSet.getString("type")) {
                    case "THREAD":
                        final ForumThread forumThread = new ForumThread(resultSet.getInt("id"),
                                resultSet.getString("title"), resultSet.getString("message"),
                                resultSet.getInt("author_id"), resultSet.getInt("author_timestamp"),
                                resultSet.getInt("state"), resultSet.getString("locked").equals("1"),
                                resultSet.getString("pinned").equals("1"), resultSet.getInt("deleter_id"),
                                resultSet.getInt("deleter_time"));

                        threads.put(forumThread.getId(), forumThread);
                        break;

                    case "REPLY":
                        final int msgId = resultSet.getInt("id");
                        final int threadId = resultSet.getInt("thread_id");

                        if (!threads.containsKey(threadId)) {
                            continue;
                        }

                        final ForumThreadReply threadReply = new ForumThreadReply(msgId, -1,
                                resultSet.getString("message"), threadId, resultSet.getInt("author_id"),
                                resultSet.getInt("author_timestamp"), resultSet.getInt("state"),
                                resultSet.getInt("deleter_id"), resultSet.getInt("deleter_time"));

                        threads.get(threadReply.getThreadId()).addReply(threadReply);

                        threadReply.setIndex(threads.get(threadReply.getThreadId()).getReplies().indexOf(threadReply));
                        break;
                }
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return threads;
    }

    public static Map<Integer, Map<Integer, Integer>> getAllPlayerViewsForGroup(int groupId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ConcurrentMap<Integer, Map<Integer, Integer>> views = Maps.newConcurrentMap();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM group_forum_views WHERE group_id = ?",
                    sqlConnection);

            preparedStatement.setInt(1, groupId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int threadId = resultSet.getInt("thread_id");

                if (!views.containsKey(threadId)) {
                    views.put(threadId, Maps.newHashMap());
                }

                views.get(threadId).put(resultSet.getInt("player_id"),
                        resultSet.getInt("timestamp"));
            }
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(resultSet);
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }

        return views;
    }

    public static void registerPlayerView(int groupId, int threadId, int playerId, int lastView) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO group_forum_views " +
                    "(`group_id`,`thread_id`,`player_id`,`timestamp`) " +
                    "VALUES (?, ?, ?, ?)",
                    sqlConnection);

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, threadId);
            preparedStatement.setInt(3, playerId);
            preparedStatement.setInt(4, lastView);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void updatePlayerView(int groupId, int threadId, int playerId, long lastView) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE group_forum_views SET timestamp = ? " +
                    "WHERE thread_id = ? AND player_id = ?",
                    sqlConnection);

            preparedStatement.setLong(1, lastView);
            preparedStatement.setInt(2, threadId);
            preparedStatement.setInt(3, playerId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static ForumThread createThread(int groupId, String title, String message, int authorId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        final int time = (int) Comet.getTime();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT INTO group_forum_messages (type, group_id, title, message, author_id, author_timestamp) VALUES('THREAD', ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, groupId);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, message);
            preparedStatement.setInt(4, authorId);
            preparedStatement.setInt(5, time);

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return new ForumThread(resultSet.getInt(1), title, message, authorId, time, 1,
                        false, false, 0, 0);
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

    public static ForumThreadReply createReply(int groupId, int threadId, String message, int authorId) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        final int time = (int) Comet.getTime();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into group_forum_messages (type, group_id, thread_id, message, " +
                    "author_id, author_timestamp) VALUES('REPLY', ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setInt(1, groupId);
            preparedStatement.setInt(2, threadId);
            preparedStatement.setString(3, message);
            preparedStatement.setInt(4, authorId);
            preparedStatement.setInt(5, time);

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();

            while (resultSet.next()) {
                return new ForumThreadReply(resultSet.getInt(1), -1, message, threadId, authorId, time,
                        1, 0, 0);
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

    public static void saveMessageState(int messageId, int state, int deleterId, int deleterTime) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE group_forum_messages SET state = ?, deleter_id = ?, deleter_time = ? WHERE id = ?", sqlConnection);

            preparedStatement.setInt(1, state);
            preparedStatement.setInt(2, deleterId);
            preparedStatement.setInt(3, deleterTime);
            preparedStatement.setInt(4, messageId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveMessageLockState(int messageId, boolean isLocked) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE group_forum_messages SET locked = ? WHERE id = ?", sqlConnection);

            preparedStatement.setString(1, isLocked ? "1" : "0");
            preparedStatement.setInt(2, messageId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static void saveMessagePinnedState(int messageId, boolean isPinned) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE group_forum_messages SET pinned = ? WHERE id = ?", sqlConnection);

            preparedStatement.setString(1, isPinned ? "1" : "0");
            preparedStatement.setInt(2, messageId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }
}
