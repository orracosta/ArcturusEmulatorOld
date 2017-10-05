package com.habboproject.server.game.commands.user.settings;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class EnableCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        Room room = client.getPlayer().getEntity().getRoom();

        if (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        if (params.length != 1) {
            return;
        }

        String command = !params[0].contains(":") ? ":" + params[0] : params[0];

        ChatCommand chatCommand = CommandManager.getInstance().get(command);
        if (chatCommand != null) {
            if (room.getData().getDisabledCommands().contains(chatCommand.getPermission())) {
                room.getData().getDisabledCommands().remove(chatCommand.getPermission());
                room.getData().save();
                EnableCommand.sendNotif(Locale.get("command.enablecommand.success"), client);
            } else {
                client.send(new AdvancedAlertMessageComposer(Locale.get("command.enablecommand.error")));
            }
        } else {
            client.send(new AdvancedAlertMessageComposer(Locale.get("command.enablecommand.error")));
        }
    }

    @Override
    public String getPermission() {
        return "enablecommand_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.enablecommand.description");
    }
}
