package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.OneWayGateActionOne;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionOneWayGate extends HabboItem
{
    public InteractionOneWayGate(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionOneWayGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return isWalkable() && room.getHabbosAt(this.getX(), this.getY()).isEmpty();
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.getExtradata().length() == 0)
        {
            this.setExtradata("0");
            this.needsUpdate(true);
        }

        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if (client != null)
        {
            RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), this.getRotation());

            if (tile != null && tile.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                if (room.getHabbosAt(this.getX(), this.getY()).isEmpty())
                {
                    HabboItem item = this;
                    room.scheduledTasks.add(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[(getRotation() + 4) % 8]);
                            client.getHabbo().getRoomUnit().getStatus().put("mv", getX() + "," + getY() + "," + getZ());
                            client.getHabbo().getRoomUnit().animateWalk = false;
                            room.sendComposer(new RoomUserStatusComposer(client.getHabbo().getRoomUnit()).compose());
                            client.getHabbo().getRoomUnit().getStatus().remove("mv");
                            client.getHabbo().getRoomUnit().setLocation(room.getLayout().getTile(getX(), getY()));

                            setExtradata("1");
                            room.updateItem(item);
                            room.scheduledTasks.add(new OneWayGateActionOne(client, room, item));
                        }
                    });

                }
            }
        }
    }
    
    @Override
    public void onPickUp(Room room)
    {
        this.setExtradata("0");
    }
}
