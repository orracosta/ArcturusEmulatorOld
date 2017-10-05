package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;


public class TeleportCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] message) {
        if (client.getPlayer().getEntity().hasAttribute("teleport")) {
            client.getPlayer().getEntity().removeAttribute("teleport");
            sendNotif(Locale.get("command.teleport.disabled"), client);
        } else {
            client.getPlayer().getEntity().setAttribute("teleport", true);
            sendNotif(Locale.get("command.teleport.enabled"), client);
        }
    }

    @Override
    public String getPermission() {
        return "teleport_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.teleport.description");
    }
}
