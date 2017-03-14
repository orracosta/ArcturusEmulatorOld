package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;

public class RoomUserLookAtPoint extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

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

        RoomUnit roomUnit = habbo.getRoomUnit();

        if(!roomUnit.canWalk())
            return;

        if(roomUnit.isWalking() || roomUnit.getStatus().containsKey("mv"))
            return;

        if (roomUnit.cmdLay || roomUnit.getStatus().containsKey("lay"))
            return;

        int x = this.packet.readInt();
        int y = this.packet.readInt();

        if(x == roomUnit.getX() && y == roomUnit.getY())
            return;

        roomUnit.lookAtPoint(habbo.getHabboInfo().getCurrentRoom().getLayout().getTile((short) x, (short) y));

        UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.WALKED, false);
        Emulator.getPluginManager().fireEvent(event);

        if (!event.isCancelled())
        {
            if (!event.idle)
            {
                this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(habbo);
            }
        }

        this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserStatusComposer(roomUnit).compose());
    }
}
