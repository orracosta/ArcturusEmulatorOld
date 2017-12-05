package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRespectComposer;

public class RoomUserGiveRespectEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();

        if(this.client.getHabbo().getHabboStats().respectPointsToGive > 0)
        {
            Habbo target = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(userId);

            if(target != null && target != this.client.getHabbo())
            {
                target.getHabboStats().respectPointsReceived++;
                this.client.getHabbo().getHabboStats().respectPointsGiven++;
                this.client.getHabbo().getHabboStats().respectPointsToGive--;
                this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserRespectComposer(target).compose());
                this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserActionComposer(this.client.getHabbo().getRoomUnit(), RoomUserAction.THUMB_UP).compose());

                AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RespectGiven"));
                AchievementManager.progressAchievement(target, Emulator.getGameEnvironment().getAchievementManager().getAchievement("RespectEarned"));

                this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(this.client.getHabbo());
            }
        }
    }
}
