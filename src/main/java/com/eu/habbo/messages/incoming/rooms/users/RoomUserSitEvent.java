package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.messages.incoming.MessageHandler;

/**
 * Created on 21-1-2015 10:13.
 */
public class RoomUserSitEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            if(this.client.getHabbo().getRoomUnit().isWalking())
            {
                this.client.getHabbo().getRoomUnit().stopWalking();
            }
            this.client.getHabbo().getHabboInfo().getCurrentRoom().makeSit(this.client.getHabbo());
            this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(this.client.getHabbo());
        }
    }
}
