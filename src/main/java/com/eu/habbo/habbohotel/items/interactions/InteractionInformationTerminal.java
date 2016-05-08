package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;
import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 24-1-2016 11:49.
 */
public class InteractionInformationTerminal extends InteractionCustomValues
{
    public static THashMap<String, String> defaultValues = new THashMap<String, String>()
    {
        {put("internalLink", "http://arcturus.wf");}
    };

    public InteractionInformationTerminal(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, defaultValues);
    }

    public InteractionInformationTerminal(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, defaultValues);
    }
}
