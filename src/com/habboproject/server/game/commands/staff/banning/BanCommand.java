package com.habboproject.server.game.commands.staff.banning;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.types.BanType;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.sessions.Session;


public class BanCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            return;
        }

        String username = params[0];
        int length = Integer.parseInt(params[1]);

        Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable()) {
            return;
        }

        client.getPlayer().getStats().setBans(client.getPlayer().getStats().getBans() + 1);

        user.disconnect("banned");

        long expire = Comet.getTime() + (length * 3600);

        BanManager.getInstance().banPlayer(BanType.USER, user.getPlayer().getId() + "", length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId());
    }

    @Override
    public String getPermission() {
        return "ban_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.ban.description");
    }
}
