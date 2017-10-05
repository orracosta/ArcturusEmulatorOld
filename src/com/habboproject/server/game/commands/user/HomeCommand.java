package com.habboproject.server.game.commands.user;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class HomeCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getSettings().getHomeRoom() > 0) {
            client.send(new RoomForwardMessageComposer(client.getPlayer().getSettings().getHomeRoom()));
        }
    }

    @Override
    public String getPermission() {
        return "home_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.home.description");
    }
}
