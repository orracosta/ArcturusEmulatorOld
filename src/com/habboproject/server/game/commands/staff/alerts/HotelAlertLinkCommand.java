package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class HotelAlertLinkCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            sendNotif(Locale.getOrDefault("command.hotelalertlink.args", "This command requires at least 2 arguments!"), client);
        }

        final String link = params[0];

        NetworkManager.getInstance().getSessions().broadcast(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.hotelalertlink.title", "Alert"), this.merge(params, 1) + "<br><br><i> " + client.getPlayer().getData().getUsername() + "</i>", Locale.getOrDefault("command.hotelalertlink.buttontitle", "Click here"), link, ""));
    }

    @Override
    public String getPermission() {
        return "hotelalertlink_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.hotelalertlink.description");
    }
}
