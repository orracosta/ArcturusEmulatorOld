package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;

public class MovePetEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        AbstractPet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(this.packet.readInt());

        if (pet != null)
        {
            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            if (room != null && room.hasRights(this.client.getHabbo()))
            {
                if (pet.getRoomUnit() != null)
                {
                    int x = this.packet.readInt();
                    int y = this.packet.readInt();

                    RoomTile tile = room.getLayout().getTile((short)x, (short)y);

                    if (tile != null)
                    {
                        pet.getRoomUnit().setLocation(tile);
                        pet.getRoomUnit().setZ(this.packet.readInt());
                        room.sendComposer(new RoomUserStatusComposer(pet.getRoomUnit()).compose());
                        pet.needsUpdate = true;
                    }
                }
            }
        }
    }
}