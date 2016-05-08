package com.eu.habbo.messages.incoming.rooms.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.bots.BotPlacedEvent;
import com.eu.habbo.util.pathfinding.Tile;

/**
 * Created on 15-10-2014 12:23.
 */
public class BotPlaceEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        Bot bot = this.client.getHabbo().getHabboInventory().getBotsComponent().getBot(this.packet.readInt());

        if(bot == null)
            return;

        Emulator.getGameEnvironment().getBotManager().placeBot(bot, this.client.getHabbo(), this.client.getHabbo().getHabboInfo().getCurrentRoom(), new Tile(this.packet.readInt(), this.packet.readInt(), 0));
    }
}
