package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.ChangeNameUpdatedComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserNameChangedComposer;

import java.util.ArrayList;
import java.util.List;

public class ConfirmChangeNameEvent extends MessageHandler
{
    public static final List<String> changingUsernames = new ArrayList<>(2);

    @Override
    public void handle() throws Exception
    {
        String name = this.packet.readString();

        if (name.equals(this.client.getHabbo().getHabboStats().changeNameChecked))
        {
            HabboInfo habboInfo = HabboManager.getOfflineHabboInfo(name);

            if (habboInfo != null)
            {
                synchronized (changingUsernames)
                {
                    if (changingUsernames.contains(name))
                        return;

                    changingUsernames.add(name);
                }

                this.client.getHabbo().getHabboInfo().setUsername(name);
                this.client.getHabbo().getHabboInfo().run();

                for (Room room : Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo()))
                {
                    room.setOwnerName(name);
                    room.setNeedsUpdate(true);
                    room.save();
                }

                synchronized (changingUsernames)
                {
                    changingUsernames.remove(name);
                }

                this.client.sendResponse(new ChangeNameUpdatedComposer(this.client.getHabbo()));

                if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
                {
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserNameChangedComposer(this.client.getHabbo()).compose());
                }

                this.client.getHabbo().getMessenger().connectionChanged(this.client.getHabbo(), true, this.client.getHabbo().getHabboInfo().getCurrentRoom() != null);
            }
        }
    }
}
