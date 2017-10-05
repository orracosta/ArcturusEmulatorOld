package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class EventAlertCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            return;
        }

        NetworkManager.getInstance().getSessions().broadcastEventAlert(new AdvancedAlertMessageComposer(Locale.get("command.eventalert.alerttitle"), String.valueOf(Locale.get("command.eventalert.message").replace("%message%", this.merge(params)).replace("%username%", client.getPlayer().getData().getUsername())) + "<br><br><i> " + client.getPlayer().getData().getUsername() + "</i>", Locale.get("command.eventalert.buttontitle"), "event:navigator/goto/" + client.getPlayer().getEntity().getRoom().getId(), "game_promo_small"), new NotificationMessageComposer("frank", Locale.get("command.eventalert.message.small"), "event:navigator/goto/" + client.getPlayer().getEntity().getRoom().getId()));
    }

    @Override
    public String getPermission() {
        return "eventalert_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.eventalert.description");
    }
}
