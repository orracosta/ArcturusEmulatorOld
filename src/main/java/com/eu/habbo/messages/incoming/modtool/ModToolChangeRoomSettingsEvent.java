package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import gnu.trove.procedure.TObjectProcedure;

public class ModToolChangeRoomSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            int roomId = this.packet.readInt();

            final Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

            if (room != null)
            {
                int kickUsers = this.packet.readInt();
                int lockDoor = this.packet.readInt();
                int changeTitle = this.packet.readInt();

                if (changeTitle == 1)
                {
                    room.setName(Emulator.getTexts().getValue("hotel.room.inappropriate.title"));
                    room.setNeedsUpdate(true);
                }

                if (lockDoor == 1)
                {
                    room.setState(RoomState.LOCKED);
                    room.setNeedsUpdate(true);
                }

                if (kickUsers == 1)
                {
                    room.getCurrentHabbos().forEachValue(new TObjectProcedure<Habbo>()
                    {
                        @Override
                        public boolean execute(Habbo object)
                        {
                            if (!(object.hasPermission("acc_unkickable") || object.hasPermission("acc_supporttool") || room.isOwner(object)))
                            {
                                room.kickHabbo(object, false);
                            }

                            return true;
                        }
                    });
                }
            }
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.roomsettings").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}
