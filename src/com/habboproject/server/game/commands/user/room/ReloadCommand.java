package com.habboproject.server.game.commands.user.room;

import com.google.common.collect.Lists;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.List;

/**
 * Created by brend on 01/03/2017.
 */
public class ReloadCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        final Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || !room.getRights().hasRights(client.getPlayer().getId())) return;

        int roomId = room.getId();

        List<Player> players = Lists.newArrayList();
        for (PlayerEntity entity : room.getEntities().getPlayerEntities()) {
            players.add(entity.getPlayer());
        }

        room.getData().save();
        room.getItems().commit();

        RoomManager.getInstance().forceUnload(roomId);

        ThreadManager.getInstance().execute(new CometThread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

                for (Player player : players) {
                    if (player == null || player.getSession() == null)
                        continue;

                    player.getSession().send(new RoomForwardMessageComposer(roomId));
                }
            }
        });
    }

    @Override
    public String getPermission() {
        return "reloadroom_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.reloadroom.description");
    }
}
