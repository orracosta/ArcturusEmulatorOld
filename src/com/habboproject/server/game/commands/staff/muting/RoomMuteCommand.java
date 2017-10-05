package com.habboproject.server.game.commands.staff.muting;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.sessions.Session;


public class RoomMuteCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getEntity().getRoom().hasRoomMute()) {
            for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                playerEntity.setRoomMuted(false);
            }

            client.getPlayer().getEntity().getRoom().setRoomMute(false);
        } else {
            for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                playerEntity.setRoomMuted(true);
            }

            client.getPlayer().getEntity().getRoom().setRoomMute(true);
        }
    }

    @Override
    public String getPermission() {
        return "roommute_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roommute.description");
    }
}
