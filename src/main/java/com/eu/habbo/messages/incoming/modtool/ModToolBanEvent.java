package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.modtool.ModToolManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolBanEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Emulator.getGameEnvironment().getModToolManager().ban(this.packet.readInt(), this.client.getHabbo(), this.packet.readString(), this.packet.readInt(), ModToolBanType.ACCOUNT);
    }
}
