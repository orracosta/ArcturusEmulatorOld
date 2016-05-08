package com.eu.habbo.habbohotel.polls;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 3-11-2014 18:46.
 */
public class PollQuestion
{
    private int id;
    private int type;
    private String question;
    private THashMap<Integer, String[]> options;
    private int minSelections;

    public PollQuestion(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.type = set.getInt("type");
        this.question = set.getString("question");
        this.minSelections = set.getInt("min_selections");

        this.options = new THashMap<Integer, String[]>();

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
}
