package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;

public class RequestRoomLoadEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int roomId = this.packet.readInt();
        String password = this.packet.readString();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            Emulator.getGameEnvironment().getRoomManager().logExit(this.client.getHabbo());
            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserRemoveComposer(this.client.getHabbo().getRoomUnit()).compose());
            this.client.getHabbo().getHabboInfo().getCurrentRoom().removeHabbo(this.client.getHabbo());
            this.client.getHabbo().getHabboInfo().setCurrentRoom(null);
        }
        Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), roomId, password);
    }
}
