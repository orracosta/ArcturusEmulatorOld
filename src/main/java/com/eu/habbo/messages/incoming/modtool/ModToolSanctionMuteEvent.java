package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.ModToolIssueHandledComposer;

public class ModToolSanctionMuteEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        int cfhTopic = this.packet.readInt();

        if (this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (habbo != null)
            {
                habbo.mute(60 * 60);
                habbo.alert(message);
                this.client.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_mute.muted").replace("%user%", habbo.getHabboInfo().getUsername()));
            }
            else
            {
                this.client.sendResponse(new ModToolIssueHandledComposer(Emulator.getTexts().getValue("generic.user.not_found").replace("%user%", Emulator.getConfig().getValue("hotel.player.name"))));
            }
        }
    }
}