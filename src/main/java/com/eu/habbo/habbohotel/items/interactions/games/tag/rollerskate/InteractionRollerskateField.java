package com.eu.habbo.habbohotel.items.interactions.games.tag.rollerskate;

import com.eu.habbo.habbohotel.games.tag.RollerskateGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionRollerskateField extends InteractionTagField
{
    public InteractionRollerskateField(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, RollerskateGame.class);
    }

    public InteractionRollerskateField(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, RollerskateGame.class);
    }
}