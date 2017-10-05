package com.habboproject.server.game.commands.development;

import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;


public class ReloadMappingCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        client.getPlayer().getEntity().getRoom().getMapping().init();
    }

    @Override
    public String getPermission() {
        return "debug";
    }

    @Override
    public String getDescription() {
        return "Reloads the room map";
    }
}
