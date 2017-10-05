package com.habboproject.server.game.commands.vip;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;


public class NoFaceCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        String figure = client.getPlayer().getData().getFigure();

        if (client.getPlayer().getData().getTemporaryFigure() != null) {
            client.getPlayer().getData().setFigure(client.getPlayer().getData().getTemporaryFigure());
            client.getPlayer().getData().setTemporaryFigure(null);
            client.getPlayer().getData().save();
        } else {
            if (figure.contains("hd-")) {
                String[] head = ("hd-" + figure.split("hd-")[1].split("\\.")[0]).split("-");

                if (head.length < 2)
                    return;

                client.getPlayer().getData().setTemporaryFigure(figure);
                client.getPlayer().getData().setFigure(figure.replace(StringUtils.join(head, "-"), "hd-" + 99999 + "-" + (head.length != 3 ? head.length == 0 ? "" : head[head.length - 1] : head[2])));
            }
        }

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(client.getPlayer().getEntity()));
        client.send(new UpdateInfoMessageComposer(-1, client.getPlayer().getEntity()));
    }

    @Override
    public String getPermission() {
        return "noface_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.noface.description");
    }
}
