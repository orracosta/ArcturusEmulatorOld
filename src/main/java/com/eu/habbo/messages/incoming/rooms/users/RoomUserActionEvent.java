package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;

/**
 * Created on 13-9-2014 17:27.
 */
public class RoomUserActionEvent extends MessageHandler {
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getRoomUnit().isInRoom())
        {
            Habbo habbo = this.client.getHabbo();

            if(this.client.getHabbo().getRoomUnit().getCacheable().get("control") != null)
            {
                habbo = (Habbo)this.client.getHabbo().getRoomUnit().getCacheable().get("control");

                if(habbo.getHabboInfo().getCurrentRoom() != this.client.getHabbo().getHabboInfo().getCurrentRoom())
                {
                    habbo.getRoomUnit().getCacheable().remove("controller");
                    this.client.getHabbo().getRoomUnit().getCacheable().remove("control");
                    habbo = this.client.getHabbo();
                }
            }

            int action = this.packet.readInt();

            this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(habbo);

            if(action == 5)
            {
                this.client.getHabbo().getRoomUnit().setIdle();
            }

            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserActionComposer(habbo.getRoomUnit(), action).compose());
        }
    }
}
