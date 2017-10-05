package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class HotelAlertCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] message) {
        if (message.length == 0) {
            return;
        }

        NetworkManager.getInstance().getSessions().broadcast(new AdvancedAlertMessageComposer(Locale.get("command.hotelalert.title"), this.merge(message) + "<br><br><i> " + client.getPlayer().getData().getUsername() + "</i>"));
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getPermission() {
        return "hotelalert_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.hotelalert.description");
    }
}
