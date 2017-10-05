package com.habboproject.server.game.commands.staff.banning;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.types.BanType;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.sessions.Session;


public class MachineBanCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            return;
        }

        String username = params[0];
        int length = Integer.parseInt(params[1]);

        Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            // TODO: Use the "player_access" table to allow you to machine ban a user that's not online
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable()) {
            return;
        }

        long expire = Comet.getTime() + (length * 3600);

        String uniqueId = user.getUniqueId();

        if (BanManager.getInstance().hasBan(uniqueId, BanType.MACHINE)) {
            sendNotif("Machine ID: " + uniqueId + " is already banned.", client);
            return;
        }

        BanManager.getInstance().banPlayer(BanType.MACHINE, user.getUniqueId(), length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId());
        sendNotif("User has been machine ID banned (" + uniqueId + ")", client);

        user.disconnect("banned");
    }

    @Override
    public String getPermission() {
        return "machineban_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.machineban.description");
    }
}
