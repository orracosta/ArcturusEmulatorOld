package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.UserBadgesComposer;

/**
 * Created on 12-10-2014 15:50.
 */
public class RequestWearingBadgesEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(userId);

        if(habbo != null)
            this.client.sendResponse(new UserBadgesComposer(habbo.getHabboInventory().getBadgesComponent().getWearingBadges(), habbo.getHabboInfo().getId()));
        else
            this.client.sendResponse(new UserBadgesComposer(BadgesComponent.getBadgesOfflineHabbo(userId), userId));
    }
}
