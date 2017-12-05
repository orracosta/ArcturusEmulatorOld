package com.eu.habbo.habbohotel.polls;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.THashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PollQuestion implements ISerialize, Comparable<PollQuestion>
{
    private int id;
    private int parentId;
    private int type;
    private String question;
    private THashMap<Integer, String[]> options;
    private ArrayList<PollQuestion> subQuestions;
    private int minSelections;
    private int order;

    public PollQuestion(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.parentId = set.getInt("parent_id");
        this.type = set.getInt("type");
        this.question = set.getString("question");
        this.minSelections = set.getInt("min_selections");
        this.order = set.getInt("order");

        this.options = new THashMap<Integer, String[]>();
        this.subQuestions = new ArrayList<PollQuestion>();

        String opts = set.getString("options");

        if(this.type == 1 || this.type == 2)
        {
            for (int i = 0; i < opts.split(";").length; i++)
            {
                this.options.put(i, new String[]{opts.split(";")[i].split(":")[0], opts.split(";")[i].split(":")[1]});
            }
        }
    }

    public int getId()
    {
        return this.id;
    }

    public int getType()
    {
        return this.type;
    }

    public String getQuestion()
    {
        return this.question;
    }

    public THashMap<Integer, String[]> getOptions()
    {
        return this.options;
    }

    public int getMinSelections()
    {
        return this.minSelections;
    }

    public void addSubQuestion(PollQuestion pollQuestion)
    {
        this.subQuestions.add(pollQuestion);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt(this.id);
        message.appendInt(this.order);
        message.appendInt(this.type);
        message.appendString(this.question);
        message.appendInt(this.minSelections);
        message.appendInt(0);
        message.appendInt(this.options.size());

        if (this.type == 1 || this.type == 2)
        {
            for (Map.Entry<Integer, String[]> set : this.options.entrySet())
            {
                message.appendString(set.getValue()[0]);
                message.appendString(set.getValue()[1]);
                message.appendInt(set.getKey());
            }
        }

        if (this.parentId <= 0)
        {
            Collections.sort(this.subQuestions);
            message.appendInt(this.subQuestions.size());

            for (PollQuestion q : this.subQuestions)
            {
                q.serialize(message);
            }
        }
    }

    @Override
    public int compareTo(PollQuestion o)
    {
        return this.order - o.order;
    }
}
