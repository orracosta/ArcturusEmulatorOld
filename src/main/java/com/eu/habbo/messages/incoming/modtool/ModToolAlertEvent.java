package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolAlertEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Emulator.getGameEnvironment().getModToolManager().alert(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.packet.readInt()), this.packet.readString(), this.client.getHabbo());
    }
}
