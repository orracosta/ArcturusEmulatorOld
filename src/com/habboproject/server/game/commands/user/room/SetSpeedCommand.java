package com.habboproject.server.game.commands.user.room;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;


public class SetSpeedCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        if (!StringUtils.isNumeric(params[0]) || params[0].length() >= 10) return;

        if (client.getPlayer().getEntity() != null
                && client.getPlayer().getEntity().getRoom() != null) {
            if (!client.getPlayer().getEntity().getRoom().getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
                return;
            }

            int speed = Integer.parseInt(params[0]);

            if (speed < 0) {
                speed = 0;
            } else if (speed > 20) {
                speed = 20;
            }

            client.getPlayer().getEntity().getRoom().setAttribute("customRollerSpeed", speed);
            sendNotif(Locale.get("command.setspeed.set").replace("%s", speed + ""), client);
        }
    }

    @Override
    public String getPermission() {
        return "setspeed_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.setspeed.description");
    }
}