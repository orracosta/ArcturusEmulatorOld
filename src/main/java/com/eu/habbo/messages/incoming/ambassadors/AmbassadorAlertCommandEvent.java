package com.eu.habbo.messages.incoming.ambassadors;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;

public class AmbassadorAlertCommandEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().hasPermission("acc_ambassador"))
        {
            int userId = this.packet.readInt();

            Habbo habbo = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(userId);

            if(habbo != null)
            {
                habbo.getClient().sendResponse(new BubbleAlertComposer("ambassador.alert.warning"));
            }
        }
    }
}
