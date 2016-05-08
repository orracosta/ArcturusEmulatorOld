package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.HallOfFameComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;

/**
 * Created on 20-9-2014 15:30.
 */
public class HotelViewEvent extends MessageHandler {
    @Override
    public void handle() throws Exception
    {
        this.client.getHabbo().getHabboInfo().setLoadingRoom(0);

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(this.client.getHabbo(), this.client.getHabbo().getHabboInfo().getCurrentRoom());
        }

        if(this.client.getHabbo().getRoomUnit() != null)
        {
            this.client.getHabbo().getRoomUnit().clearWalking();
            this.client.getHabbo().getRoomUnit().setInRoom(false);
        }
    }
}
