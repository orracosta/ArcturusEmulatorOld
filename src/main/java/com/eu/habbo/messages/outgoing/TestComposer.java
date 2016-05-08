package com.eu.habbo.messages.outgoing;

import com.eu.habbo.messages.ServerMessage;

/**
 * Created on 21-9-2014 10:01.
 */
public class TestComposer extends MessageComposer {

    @Override
    public ServerMessage compose() {

        this.response.init(3662);
        this.response.appendBoolean(true);
        this.response.appendString("LOL");
        /*
        this.response.init(3269);
        this.response.appendString("cannon.png");
        this.response.appendInt32(2);
        this.response.appendString("title");
        this.response.appendString("${notification.room.kick.cannonball.title}");
        this.response.appendString("message");
        this.response.appendString("${notification.room.kick.cannonball.message}");
        */
        return this.response;

    }
}
