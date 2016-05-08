package com.eu.habbo.habbohotel.crafting;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CraftingRecipe
{
    public final int id;
    public final THashMap<String, Integer> ingredients;
    public final String result;

    public CraftingRecipe(ResultSet set) throws SQLException
    {
        this.id         = set.getInt("id");
        this.result     = set.getString("result");
        this.ingredients = new THashMap<String, Integer>();

        for(String part : set.getString("ingredients").split(";"))
        {
            String[] item = part.split(":");
            this.ingredients.put(item[0], Integer.valueOf(item[1]));
        }
    }
}
