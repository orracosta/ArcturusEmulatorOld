package com.habboproject.server.game.commands.staff.rewards.mass;

import com.habboproject.server.api.networking.sessions.BaseSession;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;


public abstract class MassCurrencyCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 1 || params[0].isEmpty() || !StringUtils.isNumeric(params[0]))
            return;

        final int amount = Integer.parseInt(params[0]);

        for (BaseSession session : NetworkManager.getInstance().getSessions().getSessions().values()) {
            try {

                if (this instanceof MassCoinsCommand) {
                    session.getPlayer().getData().increaseCredits(amount);
                    session.send(new AdvancedAlertMessageComposer(Locale.get("command.coins.title"), Locale.get("command.coins.received").replace("%amount%", String.valueOf(amount))));

                } else if (this instanceof MassDucketsCommand) {
                    session.getPlayer().getData().increaseActivityPoints(amount);

                } else if (this instanceof MassPointsCommand) {
                    session.getPlayer().getData().increasePoints(amount);

                    session.send(new AdvancedAlertMessageComposer(
                            Locale.get("command.points.successtitle"),
                            Locale.get("command.points.successmessage").replace("%amount%", String.valueOf(amount))
                    ));

                    session.send(session.getPlayer().composeCurrenciesBalance());
                }

                session.getPlayer().getData().save();
                session.getPlayer().sendBalance();
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
