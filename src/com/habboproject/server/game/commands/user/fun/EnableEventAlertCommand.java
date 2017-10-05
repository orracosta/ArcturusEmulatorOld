package com.habboproject.server.game.commands.user.fun;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;

/**
 * Created by brend on 06/03/2017.
 */
public class EnableEventAlertCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client == null || client.getPlayer() == null || client.getPlayer().getSettings() == null) {
            return;
        }

        client.getPlayer().getSettings().setEnableEventNotif(true);

        EnableEventAlertCommand.sendNotif(Locale.get("command.enableeventnotif.txt"), client);
    }

    @Override
    public String getPermission() {
        return "enableeventnotif_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.enableeventnotif.description");
    }
}
