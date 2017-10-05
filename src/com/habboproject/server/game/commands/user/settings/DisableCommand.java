package com.habboproject.server.game.commands.user.settings;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class DisableCommand extends ChatCommand {
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
            if (!client.getPlayer().getPermissions().hasCommand(chatCommand.getPermission())) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("command.disablecommand.error")));
                return;
            }

            if (!room.getData().getDisabledCommands().contains(chatCommand.getPermission())) {
                room.getData().getDisabledCommands().add(chatCommand.getPermission());
                room.getData().save();

                DisableCommand.sendNotif(Locale.get("command.disablecommand.success"), client);
            } else {
                client.send(new AdvancedAlertMessageComposer("This command is already disabled"));
            }
        } else {
            client.send(new AdvancedAlertMessageComposer(Locale.get("command.disablecommand.error")));
        }
    }

    @Override
    public String getPermission() {
        return "disablecommand_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.disablecommand.description");
    }
}
