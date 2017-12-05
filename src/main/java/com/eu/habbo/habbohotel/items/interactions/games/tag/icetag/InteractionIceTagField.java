package com.eu.habbo.habbohotel.items.interactions.games.tag.icetag;

import com.eu.habbo.habbohotel.games.tag.IceTagGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionIceTagField extends InteractionTagField
{
    public InteractionIceTagField(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, IceTagGame.class);
    }

    public InteractionIceTagField(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, IceTagGame.class);
    }
}