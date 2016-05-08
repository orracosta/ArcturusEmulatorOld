package com.eu.habbo.messages.incoming.rooms.promotions;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.promotions.PromoteOwnRoomsListComposer;

/**
 * Created on 15-11-2015 13:58.
 */
public class RequestPromotionRoomsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new PromoteOwnRoomsListComposer(Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo())));
    }
}
