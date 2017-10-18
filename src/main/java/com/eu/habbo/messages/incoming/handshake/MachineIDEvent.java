package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.handshake.MachineIDComposer;

public class MachineIDEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        this.packet.readString();
        this.client.setMachineId(this.packet.readString());
        if (this.client.getHabbo() != null && this.client.getHabbo().getHabboInfo() != null)
        {
            this.client.getHabbo().getHabboInfo().setMachineID(this.client.getMachineId());
        }

        if (Emulator.getGameEnvironment().getModToolManager().hasMACBan(this.client))
        {
            this.client.getChannel().close();
            return;
        }

        this.client.sendResponse(new MachineIDComposer(this.client));
    }
}
