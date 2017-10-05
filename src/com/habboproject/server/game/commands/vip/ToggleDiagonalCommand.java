package com.habboproject.server.game.commands.vip;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.sessions.Session;

public class ToggleDiagonalCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getEntity().getRoom().getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            sendNotif(Locale.getOrDefault("command.togglediagonal.nopermission", "You don't have permission to use this command!"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (room.hasAttribute("disableDiagonal")) {
            sendNotif(Locale.getOrDefault("command.togglediagonal.enabled", "Diagonal walking has been enabled!"), client);
            room.removeAttribute("disableDiagonal");
        } else {
            sendNotif(Locale.getOrDefault("command.togglediagonal.disabled", "Diagonal walking has been disabled!"), client);
            room.setAttribute("disableDiagonal", true);
        }
    }

    @Override
    public String getPermission() {
        return "togglediagonal_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.togglediagonal.description");
    }
}
