package com.habboproject.server.game.commands.user;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.boot.CometServer;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class CometCommand extends ChatCommand {
    @Override
    public void execute(Session client, String message[]) {
        //client.send(new AlertMessageComposer("Powered by Comet Server. <br><br><b>Waves to:</b><br>- Leon<br>- Matty<br>- Matou19<br>- Markones<br>- Helpi<br>- Luck<br>- Johno<br>- Sledmore<br>- Scott<br>- Nillus<br>- Jordan<br>- Burak<br>- Quackster<br>- Jaxter<br>- Kai<br>- Caffeine<br>- More Caffeine<br>- Mary Jane<br><br><b>Fuckings to:</b><br>- Fahd<br>- Magrao<br>- TheGeneral<br><br>Server Version: <b>" + Comet.getBuild() + "</b><br>Client Version: <b>" + CometServer.CLIENT_VERSION.replace("PRODUCTION-", "") + "</b>"));
    }

    @Override
    public String getPermission() {
        return "dev";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
