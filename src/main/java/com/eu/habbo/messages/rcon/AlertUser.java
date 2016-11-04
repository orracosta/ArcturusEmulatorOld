package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.google.gson.Gson;

public class AlertUser extends RCONMessage<AlertUser.JSONAlertUser>
{
    /**
     * Sends an alert to an online user.
     */
    public AlertUser()
    {
        super(JSONAlertUser.class);
    }

    @Override
    public void handle(Gson gson, JSONAlertUser object)
    {
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.username);

        if(habbo != null)
        {
            habbo.getClient().sendResponse(new GenericAlertComposer(object.message));
        }

        this.status = RCONMessage.HABBO_NOT_FOUND;
    }

    protected class JSONAlertUser
    {
        private String username;
        private String message;
    }
}
