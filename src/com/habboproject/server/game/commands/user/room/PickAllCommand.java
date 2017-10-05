package com.habboproject.server.game.commands.user.room;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.sessions.Session;

import java.util.ArrayList;
import java.util.List;


public class PickAllCommand extends ChatCommand {
    @Override
    public void execute(Session client, String message[]) {
        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || !room.getData().getOwner().equals(client.getPlayer().getData().getUsername())) {
            return;
        }

        List<RoomItem> itemsToRemove = new ArrayList<>();

        itemsToRemove.addAll(room.getItems().getFloorItems().values());
        itemsToRemove.addAll(room.getItems().getWallItems().values());

        for (RoomItem item : itemsToRemove) {
            if (item instanceof RoomItemFloor) {
                room.getItems().removeItem((RoomItemFloor) item, client);
            } else if (item instanceof RoomItemWall) {
                room.getItems().removeItem((RoomItemWall) item, client, true);
            }
        }

        itemsToRemove.clear();
    }

    @Override
    public String getPermission() {
        return "pickall_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.pickall.description");
    }
}
