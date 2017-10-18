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
        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(object.user_id);

        if(habbo != null)
        {
            habbo.getClient().sendResponse(new GenericAlertComposer(object.message));
        }

        this.status = RCONMessage.HABBO_NOT_FOUND;
    }

    public class JSONAlertUser
    {
        public int user_id;
        public String message;
    }
}
