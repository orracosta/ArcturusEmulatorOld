package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorEventCategoriesComposer;
import com.eu.habbo.messages.outgoing.navigator.RoomCategoriesComposer;

/**
 * Created on 25-8-2014 12:03.
 */
public class RequestRoomCategoriesEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new RoomCategoriesComposer(Emulator.getGameEnvironment().getRoomManager().roomCategoriesForHabbo(this.client.getHabbo())));
        this.client.sendResponse(new NewNavigatorEventCategoriesComposer());
    }
}
