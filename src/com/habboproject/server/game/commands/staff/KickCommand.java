package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.sessions.Session;


public class KickCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1) {
            return;
        }
        String username = params[0];

        PlayerEntity entity = (PlayerEntity) client.getPlayer().getEntity().getRoom().getEntities().getEntityByName(username, RoomEntityType.PLAYER);

        if (entity == null)
            return;

        if (entity.getUsername().equals(client.getPlayer().getData().getUsername())) {
            return;
        }

        if (!entity.getPlayer().getPermissions().getRank().roomKickable()) {

            sendNotif(Locale.get("command.kick.unkickable"), client);
            return;
        }

        entity.kick();
    }

    @Override
    public String getPermission() {
        return "kick_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.kick.description");
    }
}
