package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.plugin.events.users.UserSavedSettingsEvent;

/**
 * Created on 20-8-2015 22:41.
 */
public class SaveIgnoreRoomInvitesEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        this.client.getHabbo().getHabboStats().blockRoomInvites = this.packet.readBoolean();

        Emulator.getPluginManager().fireEvent(new UserSavedSettingsEvent(this.client.getHabbo()));
    }
}
