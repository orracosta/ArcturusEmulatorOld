package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class InvisibleCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        boolean isVisible = false;

        if (!client.getPlayer().getEntity().isVisible()) {
            isVisible = true;
        }

        client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), Locale.get("command.invisible." + (isVisible ? "disabled" : "enabled"))));

        client.getPlayer().setInvisible(!isVisible);
        client.getPlayer().getEntity().updateVisibility(isVisible);
    }

    @Override
    public String getPermission() {
        return "invisible_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.invisible.description");
    }
}
