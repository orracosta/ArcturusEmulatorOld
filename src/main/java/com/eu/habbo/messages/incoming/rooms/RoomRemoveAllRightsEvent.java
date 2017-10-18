package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomRightsComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveRightsComposer;
import gnu.trove.procedure.TIntProcedure;

public class RoomRemoveAllRightsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        final Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null || room.getId() != this.packet.readInt())
            return;

        if(room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            room.getRights().forEach(new TIntProcedure()
            {
                @Override
                public boolean execute(int value)
                {
                    Habbo habbo = room.getHabbo(value);

                    if(habbo != null)
                    {
                        room.sendComposer(new RoomUserRemoveRightsComposer(room, value).compose());
                        habbo.getRoomUnit().getStatus().remove("flatctrl");
                        habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.NONE));
                    }

                    return true;
                }
            });

            room.removeAllRights();
        }
    }
}
