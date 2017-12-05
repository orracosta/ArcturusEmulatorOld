package com.eu.habbo.habbohotel.items.interactions.games.battlebanzai;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionBattleBanzaiTimer extends InteractionGameTimer
{
    public InteractionBattleBanzaiTimer(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionBattleBanzaiTimer(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public Class<? extends Game> getGameType()
    {
        return BattleBanzaiGame.class;
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }
}
