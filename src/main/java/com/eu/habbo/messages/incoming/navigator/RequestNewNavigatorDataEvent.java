package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.*;

/**
 * Created on 19-6-2015 13:33.
 */
public class RequestNewNavigatorDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.sendResponse(new NewNavigatorMetaDataComposer());
        this.client.sendResponse(new NewNavigatorLiftedRoomsComposer());
        this.client.sendResponse(new NewNavigatorCollapsedCategoriesComposer());
        this.client.sendResponse(new NewNavigatorSavedSearchesComposer());
        this.client.sendResponse(new NewNavigatorEventCategoriesComposer());
    }
}
