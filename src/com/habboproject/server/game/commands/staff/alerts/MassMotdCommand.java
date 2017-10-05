package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class MassMotdCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] message) {
        final MotdNotificationMessageComposer msg = new MotdNotificationMessageComposer(this.merge(message) + "\n\n- " + client.getPlayer().getData().getUsername());

        NetworkManager.getInstance().getSessions().broadcast(msg);
    }

    @Override
    public String getPermission() {
        return "massmotd_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.massmotd.description");
    }
}
