package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomBannedUsersComposer;

/**
 * Created on 30-8-2015 10:42.
 */
public class RoomRequestBannedUsersEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int roomId = this.packet.readInt();

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if(room != null)
        {
            //this.client.sendResponse(new RoomBannedUsersComposer(room));
        }

        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("SelfModChatFloodFilterSeen"));
        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("SelfModChatHearRangeSeen"));
        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("SelfModChatScrollSpeedSeen"));
        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().achievements.get("SelfModDoorModeSeen"));
    }
}
