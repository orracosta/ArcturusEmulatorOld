package com.habboproject.server.game.commands.user;

import com.habboproject.server.api.commands.CommandInfo;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.modules.ModuleManager;
import com.habboproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;

import java.util.Map;


public class CommandsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        StringBuilder list = new StringBuilder();

        for (Map.Entry<String, CommandInfo> commandInfoEntry : ModuleManager.getInstance().getEventHandler().getCommands().entrySet()) {
            if (client.getPlayer().getPermissions().hasCommand(commandInfoEntry.getValue().getPermission()) || commandInfoEntry.getValue().getPermission().isEmpty()) {
                list.append(commandInfoEntry.getKey() + " - " + commandInfoEntry.getValue().getDescription() + "\n");
            }
        }

        for (Map.Entry<String, ChatCommand> command : CommandManager.getInstance().getChatCommands().entrySet()) {
            if (command.getValue().isHidden()) continue;

            if (client.getPlayer().getPermissions().hasCommand(command.getValue().getPermission())) {
                list.append(command.getKey().split(",")[0] + " - " + command.getValue().getDescription() + "\n");
            }
        }

        client.send(new MotdNotificationMessageComposer(Locale.get("command.commands.title") + "\n================================================\n" + list.toString()));
    }

    @Override
    public String getPermission() {
        return "commands_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.commands.description");
    }
}
