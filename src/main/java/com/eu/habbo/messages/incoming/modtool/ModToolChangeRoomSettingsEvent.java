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
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.packet.readInt());

        if (room != null)
        {
            Emulator.getGameEnvironment().getModToolManager().roomAction(room, this.client.getHabbo(), this.packet.readInt() == 1, this.packet.readInt() == 1, this.packet.readInt() == 1);
        }
    }
}
