package com.eu.habbo.habbohotel.polls;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 3-11-2014 18:46.
 */
public class PollManager
{
    private static final THashMap<Integer, Poll> activePolls = new THashMap<Integer, Poll>();

    private static void loadPoll(int id)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT polls.title, polls.reward_badge, polls.thanks_message, polls_questions.* FROM polls INNER JOIN polls_questions ON polls.id = polls_questions.poll_id WHERE polls.id = ? ORDER BY polls_questions.question_number ASC");
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            activePolls.put(id, new Poll(set));
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public static Poll getPoll(int pollId)
    {
        Poll poll = activePolls.get(pollId);

        if(poll == null)
        {
            loadPoll(pollId);
            poll = activePolls.get(pollId);
        }

        return poll;
    }

    public static boolean donePoll(Habbo habbo, int pollId)
    {
        boolean done = false;
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT NULL FROM polls_answers WHERE poll_id = ? AND user_id = ? LIMIT 1");
            statement.setInt(1, pollId);
            statement.setInt(2, habbo.getHabboInfo().getId());
            ResultSet set = statement.executeQuery();
            while(set.next())
            {
                done = true;
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        return done;
    }
}
