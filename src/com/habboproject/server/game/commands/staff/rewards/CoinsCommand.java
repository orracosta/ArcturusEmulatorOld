package com.habboproject.server.game.commands.staff.rewards;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class CoinsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2)
            return;

        String username = params[0];

        try {
            int credits = Integer.parseInt(params[1]);
            Session player = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

            if (player == null) {
                PlayerData playerData = PlayerDao.getDataByUsername(username);

                if (playerData == null) return;

                playerData.increaseCredits(credits);
                playerData.save();
                return;
            }

            player.getPlayer().getData().increaseCredits(credits);
            player.send(new AdvancedAlertMessageComposer(Locale.get("command.coins.title"), Locale.get("command.coins.received").replace("%amount%", String.valueOf(credits))));

            player.getPlayer().getData().save();
            player.getPlayer().sendBalance();
        } catch (Exception e) {
            client.send(new AdvancedAlertMessageComposer(Locale.get("command.coins.errortitle"), Locale.get("command.coins.formaterror")));
        }
    }

    @Override
    public String getPermission() {
        return "coins_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.coins.description");
    }
}
