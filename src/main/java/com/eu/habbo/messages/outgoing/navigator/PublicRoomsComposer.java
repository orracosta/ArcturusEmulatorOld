package com.eu.habbo.messages.outgoing.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.PublicRoom;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public class PublicRoomsComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.PublicRoomsComposer);

        synchronized (Emulator.getGameEnvironment().getNavigatorManager().getPublicRooms())
        {

            int count = 0;
            for (Map.Entry<PublicRoom, THashSet<PublicRoom>> map : Emulator.getGameEnvironment().getNavigatorManager().getPublicRooms().entrySet())
            {
                count++;
                count += map.getValue().size();
            }
            this.response.appendInt32(count);

            for (Map.Entry<PublicRoom, THashSet<PublicRoom>> map : Emulator.getGameEnvironment().getNavigatorManager().getPublicRooms().entrySet())
            {
                PublicRoom parent = map.getKey();
                this.response.appendInt32(parent.id);
                this.response.appendString(parent.caption);
                this.response.appendString(parent.description);
                this.response.appendInt32(parent.imageType);
                this.response.appendString(parent.caption);
                this.response.appendString(parent.image);
                this.response.appendInt32(parent.parentId > 0 ? parent.parentId : 0);
                this.response.appendInt32(0); //Users in room.
                this.response.appendInt32(parent.roomId == 0 ? 4 : 2);

                if (parent.roomId == 0 && parent.parentId == -1)
                {
                    this.response.appendBoolean(false);
                    for (PublicRoom room : map.getValue())
                    {
                        this.response.appendInt32(room.id);
                        this.response.appendString(room.caption);
                        this.response.appendString(room.description);
                        this.response.appendInt32(room.imageType);
                        this.response.appendString(room.caption);
                        this.response.appendString(room.image);
                        this.response.appendInt32(room.parentId > 0 ? room.parentId : 0);
                        this.response.appendInt32(0); //Users in room.
                        this.response.appendInt32(room.roomId == 0 ? 4 : 2);
                    }
                } else
                {
                    this.response.appendInt32(1); //room ID
                    this.response.appendString("room_name");
                    this.response.appendInt32(1); //owner ID
                    this.response.appendString("Admin");
                    this.response.appendInt32(0); //State
                    this.response.appendInt32(0); //In Room
                    this.response.appendInt32(50); //Max In Room
                    this.response.appendString("Description");
                    this.response.appendInt32(0);
                    this.response.appendInt32(2);
                    this.response.appendInt32(0); //Score
                    this.response.appendInt32(0);
                    this.response.appendInt32(1); //Category ID
                    this.response.appendInt32(0);
                    this.response.appendString("");
                    this.response.appendString("");
                    this.response.appendString("");

                    this.response.appendInt32(0); //Tag Count
                    this.response.appendInt32(0);
                    this.response.appendInt32(0);
                    this.response.appendInt32(0);
                    this.response.appendBoolean(false);
                    this.response.appendBoolean(false);
                    this.response.appendInt32(0);
                    this.response.appendInt32(0);
                }
            }

            this.response.appendInt32(0);
            this.response.appendInt32(0);

        }
            return this.response;
    }
}
