package com.habboproject.server.game.commands.staff.rewards;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;


public class DucketsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2 || !StringUtils.isNumeric(params[1]))
            return;

        String username = params[0];
        int duckets = Integer.parseInt(params[1]);

        Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            return;
        }

        session.getPlayer().getData().increaseActivityPoints(duckets);
        session.getPlayer().getData().save();

        session.send(new AdvancedAlertMessageComposer(
                Locale.get("command.duckets.successtitle"),
                Locale.get("command.duckets.successmessage").replace("%amount%", String.valueOf(duckets))
        ));

        session.send(new UpdateActivityPointsMessageComposer(session.getPlayer().getData().getActivityPoints(), duckets));
        session.send(session.getPlayer().composeCurrenciesBalance());
    }

    @Override
    public String getPermission() {
        return "duckets_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.duckets.description");
    }
}
