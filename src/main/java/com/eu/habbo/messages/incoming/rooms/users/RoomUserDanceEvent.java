package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDanceComposer;

/**
 * Created on 13-9-2014 17:34.
 */
public class RoomUserDanceEvent extends MessageHandler {
    @Override
    public void handle() throws Exception
    {
        int danceId = this.packet.readInt();
        if(danceId >= 0 && danceId <= 5)
        {
            if (this.client.getHabbo().getRoomUnit().isInRoom())
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

                habbo.getRoomUnit().setDanceType(DanceType.values()[danceId]);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(habbo);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserDanceComposer(habbo.getRoomUnit()).compose());
            }
        }
    }
}
