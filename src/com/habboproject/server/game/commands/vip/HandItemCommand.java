package com.habboproject.server.game.commands.vip;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;

public class HandItemCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        if (!StringUtils.isNumeric(params[0])) {
            return;
        }

        int handItem = Integer.parseInt(params[0]);

        if (handItem > 0) {
            client.getPlayer().getEntity().carryItem(handItem, false);
        }
    }

    @Override
    public String getPermission() {
        return Locale.get("handitem_command");
    }

    @Override
    public String getDescription() {
        return Locale.get("command.handitem.description");
    }
}
