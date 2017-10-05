package com.habboproject.server.game.commands.staff.alerts;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class RoomAlertCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        for (PlayerEntity entity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
            entity.getPlayer().getSession().send(new AlertMessageComposer(this.merge(params)));
        }
    }

    @Override
    public String getPermission() {
        return "roomalert_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roomalert.description");
    }
}
