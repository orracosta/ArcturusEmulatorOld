package com.eu.habbo.habbohotel.polls;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 3-11-2014 18:46.
 */
public class Poll
{
    private int id;
    private String title;
    private String thanksMessage;
    private String badgeReward;

    private THashMap<Integer, PollQuestion> questions;

    public Poll(ResultSet set) throws SQLException
    {
        set.first();
        this.id = set.getInt("poll_id");
        this.title = set.getString("title");
        this.thanksMessage = set.getString("thanks_message");
        this.badgeReward = set.getString("reward_badge");

        this.questions = new THashMap<Integer, PollQuestion>();

        set.beforeFirst();
        while(set.next())
        {
            this.questions.put(set.getInt("question_number"), new PollQuestion(set));
        }
    }

    public int getId()
    {
        return this.id;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getThanksMessage()
    {
        return this.thanksMessage;
    }

    public String getBadgeReward()
    {
        return this.badgeReward;
    }

    public THashMap<Integer, PollQuestion> getQuestions()
    {
        return this.questions;
    }
}
