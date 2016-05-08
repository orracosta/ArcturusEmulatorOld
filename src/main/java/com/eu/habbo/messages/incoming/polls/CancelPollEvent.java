package com.eu.habbo.messages.incoming.polls;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollManager;
import com.eu.habbo.messages.incoming.MessageHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created on 3-11-2014 18:39.
 */
public class CancelPollEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int pollId = this.packet.readInt();


        Poll poll = PollManager.getPoll(pollId);

        if(poll != null)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO polls_answers (poll_id, user_id, question_id, answer) VALUES (?, ?, ?, ?)");
                statement.setInt(1, pollId);
                statement.setInt(2, this.client.getHabbo().getHabboInfo().getId());
                statement.setInt(3, 0);
                statement.setString(4, "");
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
