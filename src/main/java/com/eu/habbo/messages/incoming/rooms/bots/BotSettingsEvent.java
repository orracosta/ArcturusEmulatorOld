package com.eu.habbo.messages.incoming.rooms.bots;

import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.BotSettingsComposer;

public class BotSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room == null)
            return;

        if (room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            int botId = this.packet.readInt();

            Bot bot = room.getBot(Math.abs(botId));

            if (bot == null)
                return;

            this.client.sendResponse(new BotSettingsComposer(bot, this.packet.readInt()));
        }
    }
}
