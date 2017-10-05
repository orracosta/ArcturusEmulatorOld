package com.habboproject.server.game.commands.staff.rewards;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class PointsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2)
            return;

        String username = params[0];
        int points;

        try {
            points = Integer.parseInt(params[1]);
        } catch (Exception e) {
            return;
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (session == null) {
            PlayerData playerData = PlayerDao.getDataByUsername(username);

            if (playerData == null) return;

            playerData.increasePoints(points);
            playerData.save();
            return;
        }

        session.getPlayer().getData().increasePoints(points);
        session.getPlayer().getData().save();

        session.send(new AdvancedAlertMessageComposer(
                Locale.get("command.points.successtitle"),
                Locale.get("command.points.successmessage").replace("%amount%", String.valueOf(points))
        ));

        session.send(session.getPlayer().composeCurrenciesBalance());
    }

    @Override
    public String getPermission() {
        return "points_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.points.description");
    }
}
