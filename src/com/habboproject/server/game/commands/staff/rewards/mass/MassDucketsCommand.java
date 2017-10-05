package com.habboproject.server.game.commands.staff.rewards.mass;

import com.habboproject.server.config.Locale;


public class MassDucketsCommand extends MassCurrencyCommand {
    @Override
    public String getPermission() {
        return "massduckets_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.massduckets.description");
    }
}
