package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

/**
 * Created on 8-11-2014 12:44.
 */
public class MysteryPrizeComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(427);
        this.response.appendString("s");
        this.response.appendInt32(230);
        return this.response;

        //s -> floorItem. -> itemId
        //i -> wallItem. -> itemId
        //e -> effect -> effectId
        //h -> HabboClub -> 0
    }
}
