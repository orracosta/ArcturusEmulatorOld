package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.rooms.KnockKnockUnknownComposer2;

public class KnockKnockResponseEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            String username = this.packet.readString();
            boolean accepted = this.packet.readBoolean();

            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(username);

            if(habbo != null)
            {
                if (accepted)
                {
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(habbo, this.client.getHabbo().getHabboInfo().getCurrentRoom());
                }
                else
                {
                    habbo.getClient().sendResponse(new KnockKnockUnknownComposer2(habbo));
                    habbo.getClient().sendResponse(new HotelViewComposer());
                }

                this.client.getHabbo().getHabboInfo().getCurrentRoom().removeFromQueue(habbo);
                habbo.getHabboInfo().setRoomQueueId(0);
            }

        }
    }
}
