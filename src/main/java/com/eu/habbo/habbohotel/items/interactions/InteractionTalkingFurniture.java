package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.habbohotel.items.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 18-10-2015 14:32.
 */
public class InteractionTalkingFurniture extends InteractionDefault
{
    public InteractionTalkingFurniture(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTalkingFurniture(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }
}
