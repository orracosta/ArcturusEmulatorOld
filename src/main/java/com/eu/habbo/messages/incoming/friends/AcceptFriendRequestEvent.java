package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.messages.incoming.MessageHandler;

public class AcceptFriendRequestEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int count = this.packet.readInt();
        int userId;

        for(int i = 0; i < count; i++)
        {
            userId = this.packet.readInt();

            if(userId == 0)
                return;

            if(this.client.getHabbo().getMessenger().getFriends().containsKey(userId))
                continue;

            this.client.getHabbo().getMessenger().acceptFriendRequest(userId, this.client.getHabbo().getHabboInfo().getId());

            int progress = this.client.getHabbo().getHabboStats().getAchievementProgress(Emulator.getGameEnvironment().getAchievementManager().getAchievement("FriendListSize"));

            int toProgress = 1;

            Achievement achievement = Emulator.getGameEnvironment().getAchievementManager().getAchievement("FriendListSize");

            if(achievement == null)
                return;

            if (progress > 0)
            {
                toProgress = this.client.getHabbo().getMessenger().getFriends().size() - progress;

                if(toProgress < 0)
                {
                    return;
                }
            }

            AchievementManager.progressAchievement(this.client.getHabbo(), achievement, toProgress);
        }
    }
}
