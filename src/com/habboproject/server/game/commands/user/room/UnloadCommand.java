package com.habboproject.server.game.commands.user.room;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.sessions.Session;

/**
 * Created by brend on 01/03/2017.
 */
public class UnloadCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || !room.getRights().hasRights(client.getPlayer().getId())) return;

        room.getData().save();
        room.getItems().commit();

        RoomManager.getInstance().forceUnload(room.getId());
    }

    @Override
    public String getPermission() {
        return "unloadroom_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.unloadroom.description");
    }
}
