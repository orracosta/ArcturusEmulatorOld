package com.habboproject.server.game.commands.user.fun;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;

/**
 * Created by brend on 06/03/2017.
 */
public class DisableEventAlertCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client == null || client.getPlayer() == null || client.getPlayer().getSettings() == null) {
            return;
        }

        client.getPlayer().getSettings().setEnableEventNotif(false);

        DisableEventAlertCommand.sendNotif(Locale.get("command.disableeventnotif.txt"), client);
    }

    @Override
    public String getPermission() {
        return "disableeventnotif_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.disableeventnotif.description");
    }
}
