package com.habboproject.server.game.commands.user;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.sessions.Session;


public class LayCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        PlayerEntity playerEntity = client.getPlayer().getEntity();
        if (playerEntity.hasStatus(RoomEntityStatus.LAY)) {
            playerEntity.removeStatus(RoomEntityStatus.LAY);
            playerEntity.markNeedsUpdate();
        } else {
            playerEntity.addStatus(RoomEntityStatus.LAY, "0.5");
            playerEntity.markNeedsUpdate();
        }
    }

    @Override
    public String getPermission() {
        return "lay_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.lay.description");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
