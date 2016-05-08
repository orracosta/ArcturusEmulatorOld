package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.google.gson.Gson;

/**
 * Created on 29-8-2015 12:36.
 */
public class HotelAlert extends RCONMessage<HotelAlert.JSONHotelAlert>
{
    public HotelAlert()
    {
        super(JSONHotelAlert.class);
    }

    @Override
    public String handle(JSONHotelAlert object)
    {
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new GenericAlertComposer(object.message));

        return new Gson().toJson("OK", String.class);
    }

    public class JSONHotelAlert
    {
        private String message;
    }
}
