package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class NotificationCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        String image = Comet.getServer().getConfig().get("comet.notification.avatar.prefix");
        String message = this.merge(params);

        NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer(
                image.replace("{0}", client.getPlayer().getData().getUsername()), message));
    }

    @Override
    public String getPermission() {
        return "notification_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.notification.description");
    }
}
