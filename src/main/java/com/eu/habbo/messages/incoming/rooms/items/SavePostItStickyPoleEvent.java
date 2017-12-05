package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.messages.incoming.MessageHandler;

public class SavePostItStickyPoleEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        if(itemId == -1234)
        {
            this.packet.readString();
            this.packet.readString();
            if(this.client.getHabbo().hasPermission("cmd_multi"))
            {
                String[] commands = this.packet.readString().split("\r");

                for (String command : commands)
                {
                    command.replace("<br>", "\r");
                    CommandHandler.handleCommand(this.client, command);
                }
            }
            else
            {
                Emulator.getLogging().logUserLine("Scripter Alert! " + this.client.getHabbo().getHabboInfo().getUsername() + " | " + this.packet.readString());
            }
        }
    }
}
