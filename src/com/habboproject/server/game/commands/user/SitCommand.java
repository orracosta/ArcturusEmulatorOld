package com.habboproject.server.game.commands.user;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.sessions.Session;


public class SitCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        PlayerEntity playerEntity = client.getPlayer().getEntity();
        if (!playerEntity.hasStatus(RoomEntityStatus.SIT)) {
            double height = 0.5;

            //for (RoomItemFloor roomItemFloor : playerEntity.getRoom().getItems().getItemsOnSquare(playerEntity.getPosition().getX(), playerEntity.getPosition().getY())) {
            //    height += roomItemFloor.getHeight();
            //}

            int rotation = playerEntity.getBodyRotation();

            switch (rotation) {
                case 1: {
                    rotation++;
                    break;
                }
                case 3: {
                    rotation++;
                    break;
                }
                case 5: {
                    rotation++;
                }
            }

            playerEntity.addStatus(RoomEntityStatus.SIT, String.valueOf(height));
            playerEntity.setBodyRotation(rotation);
            playerEntity.markNeedsUpdate();
        }
    }

    @Override
    public String getPermission() {
        return "sit_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.sit.description");
    }
}
