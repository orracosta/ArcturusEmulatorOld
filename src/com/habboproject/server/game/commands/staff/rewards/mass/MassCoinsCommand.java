package com.habboproject.server.game.commands.staff.rewards.mass;

import com.habboproject.server.config.Locale;


public class MassCoinsCommand extends MassCurrencyCommand {
    @Override
    public String getPermission() {
        return "masscoins_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.masscoins.description");
    }
}
