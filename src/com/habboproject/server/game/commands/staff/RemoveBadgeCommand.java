package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.sessions.Session;


public class RemoveBadgeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2)
            return;

        Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(params[0]);

        if (session != null)
            session.getPlayer().getInventory().removeBadge(params[1], true);
    }

    @Override
    public String getPermission() {
        return "removebadge_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.removebadge.description");
    }
}
