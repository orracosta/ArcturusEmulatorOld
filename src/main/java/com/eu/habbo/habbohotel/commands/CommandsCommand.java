package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MessagesForYouComposer;

import java.util.List;

public class CommandsCommand extends Command
{
    public CommandsCommand()
    {
        super("cmd_commands", Emulator.getTexts().getValue("commands.keys.cmd_commands").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        String message = "Your Commands";
        List<Command> commands = Emulator.getGameEnvironment().getCommandHandler().getCommandsForRank(gameClient.getHabbo().getHabboInfo().getRank().getId());
        message += "(" + commands.size() + "):\r\n";

        for(Command c : commands)
        {
            message += Emulator.getTexts().getValue("commands.description." + c.permission, "commands.description." + c.permission) + "\r";
        }

        gameClient.getHabbo().alert(new String[]{message});

        return true;
    }
}
