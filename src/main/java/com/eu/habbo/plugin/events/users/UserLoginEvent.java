package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;

import java.net.SocketAddress;

public class UserLoginEvent extends UserEvent
{
    /**
     * Remote IP Adress of the Habbo who logged in.
     * @see java.net.SocketAddress
     */
    public final SocketAddress ip;

    /**
     * This event is triggered whenever a Habbo logs into the client.
     * @param habbo The Habbo this event applies to.
     * @param ip The SocketAddress of the Habbo connecting.
     */
    public UserLoginEvent(Habbo habbo, SocketAddress ip)
    {
        super(habbo);

        this.ip = ip;
    }
}
