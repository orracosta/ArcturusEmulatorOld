package com.eu.habbo.habbohotel.pets;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PetCommand implements Comparable<PetCommand>
{
    public final int id;
    public final String key;
    public final int level;
    public final int xp;
    public final int energyCost;
    public final int happynessCost;

    public PetCommand(ResultSet set) throws SQLException
    {
        this.id = set.getInt("command_id");
        this.key = set.getString("text");
        this.level = set.getInt("required_level");
        this.xp = set.getInt("reward_xp");
        this.energyCost = set.getInt("cost_energy");
        this.happynessCost = set.getInt("cost_happyness");
    }

    @Override
    public int compareTo(PetCommand o)
    {
        return this.level - o.level;
    }
}