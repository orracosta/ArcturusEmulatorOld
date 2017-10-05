package com.habboproject.server.game.commands.misc;

import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;

/**
 * Created by brend on 12/03/2017.
 */
public class BigFixCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        client.send(new MotdNotificationMessageComposer("OH NÃO!\n\nTemos sérios problemas no nosso hotel?! Não entre em pânico, estamos fazendo o possível para arrumar todos eles!\n" +
                "Iremos tomar atitudes severas contra os responsáveis!\nMalditos estagiários!\nSempre eles!\n\n - Frank"));
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public String getPermission() {
        return "dev";
    }

    @Override
    public String getDescription() {
        return "";
    }
}
