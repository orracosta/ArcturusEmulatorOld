package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.habbohotel.users.HabboNavigatorWindowSettings;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 20-11-2015 20:37.
 */
public class NewNavigatorSettingsComposer extends MessageComposer
{
    private final HabboNavigatorWindowSettings windowSettings;

    public NewNavigatorSettingsComposer(HabboNavigatorWindowSettings windowSettings)
    {
        this.windowSettings = windowSettings;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewNavigatorSettingsComposer);
        this.response.appendInt32(this.windowSettings.x);
        this.response.appendInt32(this.windowSettings.y);
        this.response.appendInt32(this.windowSettings.width);
        this.response.appendInt32(this.windowSettings.height);
        this.response.appendBoolean(this.windowSettings.openSearches);
        this.response.appendInt32(0);
        return this.response;
    }
}
