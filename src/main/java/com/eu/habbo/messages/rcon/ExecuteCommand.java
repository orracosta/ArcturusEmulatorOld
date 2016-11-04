package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

public class ExecuteCommand extends RCONMessage<ExecuteCommand.JSONExecuteCommand>
{
    public ExecuteCommand(Class<JSONExecuteCommand> type)
    {
        super(type);
    }

    @Override
    public void handle(Gson gson, JSONExecuteCommand json)
    {
        try
        {
            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(json.user_id);

            if (habbo != null)
            {
                status = HABBO_NOT_FOUND;
                return;
            }


            CommandHandler.handleCommand(habbo.getClient(), json.command);
        }
        catch (Exception e)
        {
            status = STATUS_ERROR;
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public class JSONExecuteCommand
    {
        public int user_id;
        public String command;
    }
}