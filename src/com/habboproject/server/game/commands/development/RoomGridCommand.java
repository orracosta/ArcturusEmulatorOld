package com.habboproject.server.game.commands.development;

import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class RoomGridCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        client.send(new AdvancedAlertMessageComposer("Item Grid", client.getPlayer().getEntity().getRoom().getMapping().visualiseEntityGrid()));
    }

    @Override
    public String getPermission() {
        return "dev";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
