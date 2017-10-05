package com.habboproject.server.storage.queries.polls;

import com.habboproject.server.game.polls.types.Poll;
import com.habboproject.server.game.polls.types.PollQuestionType;
import com.habboproject.server.game.polls.types.questions.MultipleChoiceQuestion;
import com.habboproject.server.game.polls.types.questions.WordedPollQuestion;
import com.habboproject.server.storage.SqlHelper;
import com.google.common.collect.Maps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PollDao {
    public static Map<Integer, Poll> getAllPolls() {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        Map<Integer, Poll> data = Maps.newConcurrentMap();

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("SELECT pQ.id AS id, p.id AS pollId, p.title AS pollTitle, p.thanks_message AS pollThanksMessage, p.room_id AS pollRoomId," +
                    " p.badge_reward AS badgeReward, pQ.question_type AS questionType, pQ.question AS question, pQ.options AS options FROM polls_questions pQ LEFT JOIN polls p ON" +
                    "(SELECT id FROM polls WHERE id = pQ.poll_id);", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                final int pollId = resultSet.getInt("pollId");
                Poll poll;

                if(!data.containsKey(pollId)) {
                    final String title = resultSet.getString("pollTitle");
                    final int roomId = resultSet.getInt("pollRoomId");
                    final String thanksMessage = resultSet.getString("pollThanksMessage");
                    final String badgeReward = resultSet.getString("badgeReward");

                    poll = new Poll(pollId, roomId, title, thanksMessage, badgeReward);
                    data.put(pollId, poll);
                } else {
                    poll = data.get(pollId);
                }

                PollQuestionType questionType = PollQuestionType.valueOf(resultSet.getString("questionType"));

                switch (questionType) {
                    default:
                    case WORDED:
                        poll.addQuestion(resultSet.getInt("id"), new WordedPollQuestion(resultSet.getString("question")));
                        break;

                    case MULTIPLE_CHOICE:
                        poll.addQuestion(resultSet.getInt("id"), new MultipleChoiceQuestion(resultSet.getString("question"), resultSet.getString("options")));
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

        return data;
    }

    public static void saveAnswer(int playerId, int pollId, int questionId, String answer) {
        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            sqlConnection = SqlHelper.getConnection();

            preparedStatement = SqlHelper.prepare("INSERT into polls_answers (player_id, poll_id, question_id, answer) VALUES(?, ?, ?, ?);", sqlConnection);

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, pollId);
            preparedStatement.setInt(3, questionId);
            preparedStatement.setString(4, answer);

            preparedStatement.execute();
        } catch (SQLException e) {
            SqlHelper.handleSqlException(e);
        } finally {
            SqlHelper.closeSilently(preparedStatement);
            SqlHelper.closeSilently(sqlConnection);
        }
    }

    public static boolean hasAnswered(int playerId, int pollId, int questionId) {
            Connection sqlConnection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                sqlConnection = SqlHelper.getConnection();

                String query = "SELECT NULL FROM polls_answers WHERE question_id = ? AND poll_id = ? AND player_id = ?;";
                preparedStatement = SqlHelper.prepare(query, sqlConnection);

                preparedStatement.setInt(1, questionId);
                preparedStatement.setInt(2, pollId);
                preparedStatement.setInt(3, playerId);

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

}
