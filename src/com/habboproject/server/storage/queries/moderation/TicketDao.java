package com.habboproject.server.storage.queries.moderation;

import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.game.moderation.types.tickets.HelpTicketState;
import com.habboproject.server.game.rooms.types.components.types.ChatMessage;
import com.habboproject.server.storage.SqlHelper;
import com.habboproject.server.utilities.JsonFactory;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TicketDao {
    public static Map<Integer, HelpTicket> getOpenTickets() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, HelpTicket> data = new HashMap<>();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT * FROM moderation_help_tickets WHERE state = 'OPEN' OR state = 'IN_PROGRESS'", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                List<ChatMessage> chatMessages;
                String chatMessageString = resultSet.getString("chat_messages");

                if (chatMessageString == null || chatMessageString.isEmpty()) {
                    chatMessages = Lists.newArrayList();
                } else {
                    chatMessages = JsonFactory.getInstance().fromJson(chatMessageString, new TypeToken<ArrayList<ChatMessage>>() {
                    }.getType());
                }

                data.put(resultSet.getInt("id"), new HelpTicket(resultSet.getInt("id"), resultSet.getInt("category_id"), resultSet.getInt("timestamp_opened"), resultSet.getInt("timestamp_closed"), resultSet.getInt("submitter_id"),
                        resultSet.getInt("reported_id"), resultSet.getInt("moderator_id"), resultSet.getString("message"), HelpTicketState.valueOf(resultSet.getString("state")), chatMessages, resultSet.getInt("room_id")));
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

    public static void saveTicket(HelpTicket helpTicket) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("UPDATE moderation_help_tickets SET state = ?, submitter_id = ?, reported_id = ?, moderator_id = ?, category_id = ?, message = ?, chat_messages = ?, room_id = ?, timestamp_opened = ?, " +
                    "timestamp_closed = ? WHERE id = ?", sqlConnection);

            preparedStatement.setString(1, helpTicket.getState().toString());
            preparedStatement.setInt(2, helpTicket.getSubmitterId());
            preparedStatement.setInt(3, helpTicket.getReportedId());
            preparedStatement.setInt(4, helpTicket.getModeratorId());
            preparedStatement.setInt(5, helpTicket.getCategoryId());
            preparedStatement.setString(6, helpTicket.getMessage());
            preparedStatement.setString(7, JsonFactory.getInstance().toJson(helpTicket.getChatMessages()));
            preparedStatement.setInt(8, helpTicket.getRoomId());
            preparedStatement.setInt(9, helpTicket.getDateSubmitted());
            preparedStatement.setInt(10, helpTicket.getDateClosed());

            preparedStatement.setInt(11, helpTicket.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static int createTicket(int submitterId, String message, int category, int reportedId, int timestamp, int roomId, List<ChatMessage> chatMessages) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into moderation_help_tickets (`state`, `submitter_id`, `reported_id`, `category_id`, `message`, `chat_messages`, `room_id`, `timestamp_opened`) VALUES(?, ?, ?, ?, ?, ?, ?, ?);", sqlConnection, true);

            preparedStatement.setString(1, "OPEN");
            preparedStatement.setInt(2, submitterId);
            preparedStatement.setInt(3, reportedId);
            preparedStatement.setInt(4, category);
            preparedStatement.setString(5, message);
            preparedStatement.setString(6, JsonFactory.getInstance().toJson(chatMessages));
            preparedStatement.setInt(7, roomId);
            preparedStatement.setInt(8, timestamp);

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
}
