package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomDataComposer;
import com.eu.habbo.messages.outgoing.rooms.RoomPaneComposer;

public class RoomStaffPickEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_staff_pick"))
        {
            int roomId = this.packet.readInt();

            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

            if(room != null)
            {
                room.setStaffPromotedRoom(!room.isStaffPromotedRoom());
                room.setNeedsUpdate(true);

                if(room.isStaffPromotedRoom())
                {
                    Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(room.getOwnerId());

                    if(habbo != null)
                    {
                        AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("Spr"));
                    }
                }

                this.client.sendResponse(new RoomDataComposer(room, this.client.getHabbo(), false, false));
            }
        }
    }
}
