package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.sessions.Session;

public class QuickPollCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if(!room.getRights().hasRights(client.getPlayer().getId())) {
            if(!client.getPlayer().getPermissions().getRank().modTool()) {
                return;
            }
        }

        String question = this.merge(params);

        if (params[0].equals("end")) {
            room.endQuestion();
        } else {
            room.startQuestion(question);
        }
    }

    @Override
    public String getPermission() {
        return "quickpoll_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.quickpoll.description");
    }
}
