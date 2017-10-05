package com.habboproject.server.game.commands.user.room;

import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomDataMessageComposer;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang.StringUtils;

public class SetMaxCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1 || !StringUtils.isNumeric(params[0])) {
            sendNotif(Locale.get("command.setmax.invalid"), client);
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();
        final boolean hasRights = room.getRights().hasRights(client.getPlayer().getId());
        final boolean isStaff = client.getPlayer().getPermissions().getRank().roomFullControl();

        if (hasRights || isStaff) {
            final int maxPlayers = Integer.parseInt(params[0]);

            if ((maxPlayers > CometSettings.roomMaxPlayers && !isStaff) || maxPlayers < 1) {
                sendNotif(Locale.get("command.setmax.toomany").replace("%i", CometSettings.roomMaxPlayers + ""), client);
                return;
            }

            room.getData().setMaxUsers(maxPlayers);

            try {
                room.getData().save();

                sendNotif(Locale.get("command.setmax.done").replace("%i", maxPlayers + ""), client);
                room.getEntities().broadcastMessage(new RoomDataMessageComposer(room));
            } catch (Exception e) {

            }
        }
    }

    @Override
    public String getPermission() {
        return "setmax_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.setmax.description");
    }
}
