package com.eu.habbo.habbohotel.polls;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class Poll
{
    private int id;
    private String title;
    private String thanksMessage;
    private String badgeReward;
    public int lastQuestionId;

    private ArrayList<PollQuestion> questions;

    public Poll(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.title = set.getString("title");
        this.thanksMessage = set.getString("thanks_message");
        this.badgeReward = set.getString("reward_badge");
        this.questions = new ArrayList<PollQuestion>();
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

    public ArrayList<PollQuestion> getQuestions()
    {
        return this.questions;
    }

    public PollQuestion getQuestion(int id)
    {
        for (PollQuestion q : this.questions)
        {
            if (q.getId() == id)
            {
                return q;
            }
        }

        return null;
    }

    public void addQuestion(PollQuestion question)
    {
        this.questions.add(question);

        Collections.sort(this.questions);
    }
}
