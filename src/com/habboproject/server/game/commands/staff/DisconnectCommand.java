package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.sessions.Session;


public class DisconnectCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) return;

        String username = params[0];

        Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            return;
        }

        if (session == client) {
            sendNotif(Locale.get("command.disconnect.himself"), client);
            return;
        }

        if (!session.getPlayer().getPermissions().getRank().disconnectable()) {
            sendNotif(Locale.get("command.disconnect.undisconnectable"), client);
            return;
        }

        session.disconnect();
    }

    @Override
    public String getPermission() {
        return "disconnect_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.disconnect.description");
    }
}
