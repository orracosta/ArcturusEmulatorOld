package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class AlertCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2)
            return;

        Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(params[0]);

        if (user == null)
            return;

        user.send(new AlertMessageComposer(this.merge(params, 1)));
    }

    @Override
    public String getPermission() {
        return "alert_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.alert.description");
    }
}
