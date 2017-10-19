package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class ExplainCommand extends Command
{
    public ExplainCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length < 2)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_explain.help"));
            return true;
        }

        Command command = CommandHandler.getCommand(strings[1]);

        if (command == null)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_explain.notfound"));
            return true;
        }

        if (!gameClient.getHabbo().hasPermission(command.permission))
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_explain.nopermission"));
            return true;
        }

        String allKeys = "";
        for (int i = 0; i < command.keys.length; i++)
        {
            allKeys += command.keys[i];

            if (i+1 < command.keys.length)
            {
                allKeys += " - ";
            }
        }
        gameClient.getHabbo().talk(Emulator.getTexts().getValue("essentials.cmd_explain.info")
                .replace("%command%", strings[1])
                .replace("%description%", Emulator.getTexts().getValue("commands.description." + command.permission))
                .replace("%keys%", allKeys));

        return true;

    }
}