package com.habboproject.server.game.commands.user;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;

public class ScreenshotCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {

    }

    @Override
    public String getPermission() {
        return "dev";
    }

    @Override
    public String getDescription() {
        return Locale.getOrDefault("command.screenshot.description", "Takes a screenshot of the room you're in.");
    }
}
