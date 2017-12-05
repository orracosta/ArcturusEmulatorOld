package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionFireworks extends HabboItem
{
    public InteractionFireworks(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionFireworks(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return this.getBaseItem().allowWalk();
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());
        super.serializeExtradata(serverMessage); //Design flaw ;(
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if(client != null && this.getExtradata().equalsIgnoreCase("2")) //2 explodes I think (0 = empty, 1 = charged, 2 = effect)
        {
            AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("FireworksCharger"));
        }
    }

    @Override
    public boolean allowWiredResetState()
    {
        return true;
    }
}
