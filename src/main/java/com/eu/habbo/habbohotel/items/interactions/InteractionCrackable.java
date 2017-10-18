package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.CrackableReward;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.CrackableExplode;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionCrackable extends HabboItem
{
    public boolean cracked = false;
    private final Object lock = new Object();

    public InteractionCrackable(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionCrackable(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.getExtradata().length() == 0)
            this.setExtradata("0");

        serverMessage.appendInt(7 + (this.isLimited() ? 256 : 0));

        serverMessage.appendString(Emulator.getGameEnvironment().getItemManager().calculateCrackState(Integer.valueOf(this.getExtradata()), Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId()), this.getBaseItem()) + "");
        serverMessage.appendInt(Integer.valueOf(this.getExtradata()));
        serverMessage.appendInt(Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId()));

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        if (client == null)
        {
            return;
        }

        synchronized (this.lock)
        {
            super.onClick(client, room, objects);

            if (!room.hasRights(client.getHabbo()))
                return;

            if (client == null)
                return;

            if (this.getRoomId() == 0)
                return;

            if (this.cracked)
                return;

            if (this.getExtradata().length() == 0)
                this.setExtradata("0");

            if (this.getBaseItem().getEffectF() > 0)
                if (client.getHabbo().getHabboInfo().getGender().equals(HabboGender.F) && this.getBaseItem().getEffectF() == client.getHabbo().getRoomUnit().getEffectId())
                    return;

            if (this.getBaseItem().getEffectM() > 0)
                if (client.getHabbo().getHabboInfo().getGender().equals(HabboGender.M) && this.getBaseItem().getEffectM() == client.getHabbo().getRoomUnit().getEffectId())
                    return;

            this.setExtradata(Integer.valueOf(this.getExtradata()) + 1 + "");
            this.needsUpdate(true);
            room.updateItemState(this);

            CrackableReward rewardData = Emulator.getGameEnvironment().getItemManager().getCrackableData(this.getBaseItem().getId());

            if (rewardData != null && !rewardData.achievementTick.isEmpty())
            {
                AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement(rewardData.achievementTick));
            }
            if (!this.cracked && Integer.valueOf(this.getExtradata()) == Emulator.getGameEnvironment().getItemManager().getCrackableCount(this.getBaseItem().getId()))
            {
                this.cracked = true;
                Emulator.getThreading().run(new CrackableExplode(room, this), 1500);

                if (rewardData != null && !rewardData.achievementCracked.isEmpty())
                {
                    AchievementManager.progressAchievement(client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement(rewardData.achievementCracked));
                }
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOn(RoomUnit client, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void onWalkOff(RoomUnit client, Room room, Object[] objects) throws Exception
    {

    }
}
