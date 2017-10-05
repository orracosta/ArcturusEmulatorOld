package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class SummonCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        final String username = params[0];

        if (!PlayerManager.getInstance().isOnline(username)) {
            client.send(new AlertMessageComposer(Locale.getOrDefault("command.summon.offline", "This player is not online!")));
            return;
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) return;

        session.send(new AlertMessageComposer(Locale.get("command.summon.summoned").replace("%summoner%", client.getPlayer().getData().getUsername())));
        session.send(new RoomForwardMessageComposer(client.getPlayer().getEntity().getRoom().getId()));

        session.getPlayer().bypassRoomAuth(true);
    }

    @Override
    public String getPermission() {
        return "summon_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.summon.description");
    }
}
