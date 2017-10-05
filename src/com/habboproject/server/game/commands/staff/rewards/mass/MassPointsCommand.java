package com.habboproject.server.game.commands.staff.rewards.mass;

import com.habboproject.server.config.Locale;


public class MassPointsCommand extends MassCurrencyCommand {
    @Override
    public String getPermission() {
        return "masspoints_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.masspoints.description");
    }
}
