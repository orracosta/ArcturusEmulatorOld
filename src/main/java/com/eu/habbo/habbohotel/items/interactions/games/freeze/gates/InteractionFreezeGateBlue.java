package com.eu.habbo.habbohotel.items.interactions.games.freeze.gates;

import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 23-1-2015 19:20.
 */
public class InteractionFreezeGateBlue extends InteractionFreezeGate
{
    public static final GameTeamColors TEAM_COLOR = GameTeamColors.BLUE;

    public InteractionFreezeGateBlue(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem, TEAM_COLOR);
    }

    public InteractionFreezeGateBlue(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells, TEAM_COLOR);
    }
}
