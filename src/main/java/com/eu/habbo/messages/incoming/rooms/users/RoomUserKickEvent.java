package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorMessagesComposer;
import com.eu.habbo.plugin.events.users.UserKickEvent;

public class RoomUserKickEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        int userId = this.packet.readInt();

        Habbo target = room.getHabbo(userId);

        if(target == null)
            return;

        UserKickEvent event = new UserKickEvent(this.client.getHabbo(), target);
        Emulator.getPluginManager().fireEvent(event);

        if(event.isCancelled())
            return;

        if(room.hasRights(this.client.getHabbo()) || this.client.getHabbo().hasPermission("acc_anyroomowner") || this.client.getHabbo().hasPermission("acc_ambassador"))
        {
            room.kickHabbo(target);
        }
    }
}
