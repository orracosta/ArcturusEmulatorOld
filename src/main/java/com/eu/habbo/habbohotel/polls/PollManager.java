package com.eu.habbo.habbohotel.polls;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PollManager
{
    private final THashMap<Integer, Poll> activePolls = new THashMap<Integer, Poll>();

    public PollManager()
    {
        this.loadPolls();
    }

    public void loadPolls()
    {
        synchronized (this.activePolls)
        {
            this.activePolls.clear();

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM polls");
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    this.activePolls.put(set.getInt("id"), new Poll(set));
                }

                set.close();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM polls_questions ORDER BY parent_id, `order` ASC");
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    Poll poll = this.getPoll(set.getInt("poll_id"));

                    if (poll != null)
                    {
                        PollQuestion question = new PollQuestion(set);

                        if (set.getInt("parent_id") <= 0)
                        {
                            poll.addQuestion(question);
                        }
                        else
                        {
                            PollQuestion parentQuestion = poll.getQuestion(set.getInt("parent_id"));

                            if (parentQuestion != null)
                            {
                                parentQuestion.addSubQuestion(question);
                            }
                        }

                        poll.lastQuestionId = question.getId();
                    }
                }

                set.close();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public Poll getPoll(int pollId)
    {
        return this.activePolls.get(pollId);
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
