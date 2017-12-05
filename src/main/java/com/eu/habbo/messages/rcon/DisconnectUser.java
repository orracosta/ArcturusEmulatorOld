package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;

public class DisconnectUser extends RCONMessage<DisconnectUser.DisconnectUserJSON>
{
    /**
     * Disconnects an user.
     */
    public DisconnectUser()
    {
        super(DisconnectUserJSON.class);
    }

    @Override
    public void handle(Gson gson, DisconnectUserJSON json)
    {
        Habbo target = null;

        if (json.user_id >= 0)
        {
            target = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.user_id);
        }
        else if (!json.username.isEmpty())
        {
            target = Emulator.getGameEnvironment().getHabboManager().getHabbo(json.username);
        }
        else
        {
            this.status = RCONMessage.HABBO_NOT_FOUND;
            return;
        }

        if(target == null)
        {
            this.status = RCONMessage.STATUS_ERROR;
            this.message = Emulator.getTexts().getValue("commands.error.cmd_disconnect.user_offline");
            return;
        }

        target.getClient().getChannel().close();
        this.message = Emulator.getTexts().getValue("commands.succes.cmd_disconnect.disconnected").replace("%user%", target.getHabboInfo().getUsername());
    }

    public class DisconnectUserJSON
    {
        public int user_id = -1;
        public String username;
    }
}