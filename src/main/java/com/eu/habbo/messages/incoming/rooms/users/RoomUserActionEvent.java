package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.plugin.events.users.UserIdleEvent;

public class RoomUserActionEvent extends MessageHandler
{
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

            if(action == 5)
            {
                UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.ACTION, true);
                Emulator.getPluginManager().fireEvent(event);

                if (!event.isCancelled())
                {
                    if (event.idle)
                    {
                        this.client.getHabbo().getRoomUnit().setIdle();
                    }
                    else
                    {
                        this.client.getHabbo().getRoomUnit().resetIdleTimer();
                    }
                }
            }
            else
            {
                UserIdleEvent event = new UserIdleEvent(this.client.getHabbo(), UserIdleEvent.IdleReason.ACTION, false);
                Emulator.getPluginManager().fireEvent(event);

                if (!event.isCancelled())
                {
                    if (!event.idle)
                    {
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().unIdle(habbo);
                    }
                }

            }

            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserActionComposer(habbo.getRoomUnit(), action).compose());
        }
    }
}
