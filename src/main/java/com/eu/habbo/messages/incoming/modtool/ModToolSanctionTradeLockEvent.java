package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;

public class ModToolSanctionTradeLockEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        int duration = this.packet.readInt();
        int cfhTopic = this.packet.readInt();

        if (this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (habbo != null)
            {
                habbo.getHabboStats().allowTrade = false;
                habbo.alert(message);
            }
            else
            {
                this.client.sendResponse(new ModToolIssueHandledComposer(Emulator.getTexts().getValue("generic.user.not_found").replace("%user%", Emulator.getConfig().getValue("hotel.player.name"))));
            }
        }
    }
}