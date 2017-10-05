package com.habboproject.server.game.commands.vip;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.Square;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.types.EntityPathfinder;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.network.sessions.Session;

import java.util.List;


public class SuperPullCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            sendNotif("Invalid username", client);
            return;
        }

        if (client.getPlayer().getEntity().isRoomMuted() || client.getPlayer().getEntity().getRoom().getRights().hasMute(client.getPlayer().getId())) {
            return;
        }

        String username = params[0];
        Session pulledSession = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (pulledSession == null) {
            return;
        }

        if (pulledSession.getPlayer().getEntity() == null) {
            return;
        }

        if (username.equals(client.getPlayer().getData().getUsername())) {
            sendNotif(Locale.get("command.pull.playerhimself"), client);
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();
        PlayerEntity pulledEntity = pulledSession.getPlayer().getEntity();

        Position squareInFront = client.getPlayer().getEntity().getPosition().squareInFront(client.getPlayer().getEntity().getBodyRotation());

        pulledEntity.setWalkingGoal(squareInFront.getX(), squareInFront.getY());

        List<Square> path = EntityPathfinder.getInstance().makePath(pulledEntity, pulledEntity.getWalkingGoal());
        pulledEntity.unIdle();

        if (pulledEntity.getWalkingPath() != null)
            pulledEntity.getWalkingPath().clear();

        pulledEntity.setWalkingPath(path);

        room.getEntities().broadcastMessage(
                new TalkMessageComposer(client.getPlayer().getEntity().getId(), Locale.get("command.pull.message").replace("%playername%", pulledEntity.getUsername()), ChatEmotion.NONE, 0)
        );
    }

    @Override
    public String getPermission() {
        return "superpull_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.superpull.description");
    }
}
