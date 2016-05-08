package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.google.gson.Gson;

/**
 * Created on 3-1-2016 14:32.
 */
public class AlertUser extends RCONMessage<AlertUser.JSONAlertUser>
{
    public AlertUser()
    {
        super(JSONAlertUser.class);
    }

    @Override
    public String handle(JSONAlertUser object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.username);

        if(habbo != null)
        {
            habbo.getClient().sendResponse(new GenericAlertComposer(object.message));
            return new Gson().toJson("OK", String.class);
        }

        return new Gson().toJson("USER_NOT_FOUND", String.class);
    }

    protected class JSONAlertUser
    {
        private String username;
        private String message;
    }
}
