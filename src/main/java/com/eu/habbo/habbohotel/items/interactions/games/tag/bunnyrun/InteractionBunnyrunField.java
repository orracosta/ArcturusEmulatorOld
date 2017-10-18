package com.eu.habbo.habbohotel.items.interactions.games.tag.bunnyrun;

import com.eu.habbo.habbohotel.games.tag.BunnyrunGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBunnyrunField  extends InteractionTagField
{
    public InteractionBunnyrunField(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, BunnyrunGame.class);
    }

    public InteractionBunnyrunField(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, BunnyrunGame.class);
    }
}