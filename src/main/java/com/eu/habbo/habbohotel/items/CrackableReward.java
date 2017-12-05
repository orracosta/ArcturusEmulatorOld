package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class CrackableReward
{
    public final int itemId;
    public final int count;
    public final Map<Integer, Map.Entry<Integer, Integer>> prizes;
    public int totalChance;
    public final String achievementTick;
    public final String achievementCracked;

    public CrackableReward(ResultSet set) throws SQLException
    {
        this.itemId = set.getInt("item_id");
        this.count = set.getInt("count");
        this.achievementTick = set.getString("achievement_tick");
        this.achievementCracked = set.getString("achievement_cracked");

        String[] data = set.getString("prizes").split(";");
        this.prizes = new HashMap<Integer, Map.Entry<Integer, Integer>>();

        this.totalChance = 0;
        for(int i = 0; i < data.length; i++)
        {
            try
            {
                int itemId = 0;
                int chance = 100;

                if (data[i].contains(":") && data[i].split(":").length == 2)
                {
                    itemId = Integer.valueOf(data[i].split(":")[0]);
                    chance = Integer.valueOf(data[i].split(":")[1]);
                }
                else
                {
                    itemId = Integer.valueOf(data[i].replace(":", ""));
                }

                this.prizes.put(itemId, new AbstractMap.SimpleEntry<Integer, Integer>(this.totalChance, this.totalChance + chance));
                this.totalChance += chance;
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    public int getRandomReward()
    {
        int random = Emulator.getRandom().nextInt(this.totalChance);

        int notFound = 0;
        for (Map.Entry<Integer, Map.Entry<Integer, Integer>> set : this.prizes.entrySet())
        {
            notFound = set.getKey();
            if (random >= set.getValue().getKey() && random < set.getValue().getValue())
            {
                return set.getKey();
            }
        }

        return notFound;
    }
}
