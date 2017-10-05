package com.habboproject.server.game.commands.staff.fun;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;

public class RollCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) return;

        if (!StringUtils.isNumeric(params[0])) {
            return;
        }

        int number = Integer.parseInt(params[0]);

        if (number < 1) number = 1;
        if (number > 6) number = 6;

        client.getPlayer().getEntity().setAttribute("diceRoll", number);
    }

    @Override
    public String getPermission() {
        return "roll_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roll.description");
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
