package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class SnowWarsFullGameStatusComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(0);
        this.response.appendInt32(0); //Unused
        this.response.appendInt32(0);
        this.response.appendInt32(0);

        //SnowWarGameObjectData
        this.response.appendInt32(1); //Count
        //{
            this.response.appendInt32(3); //type
            this.response.appendInt32(1); //id?

            this.response.appendInt32(1); //variable
            this.response.appendInt32(1); //variable
            this.response.appendInt32(1); //variable
            this.response.appendInt32(1); //variable
            this.response.appendInt32(1); //variable
            this.response.appendInt32(1); //variable
            this.response.appendInt32(1); //variable

            //1: -> 11 variables.
            //4: -> 8 variables.
            //3: -> 7 variables.
            //5: -> 19 variables.
            //2: -> 9 variables.
        //}
        return this.response;
    }
}
