package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.unknown.ItemStateComposer2;
import com.eu.habbo.threading.runnables.RoomUnitTeleportWalkToAction;
import com.eu.habbo.threading.runnables.teleport.TeleportActionOne;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class InteractionTeleport extends HabboItem
{
    private int targetId;
    private int targetRoomId;
    private int roomUnitID = 0;

    public InteractionTeleport(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionTeleport(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return this.getBaseItem().allowWalk() || roomUnit.getId() == roomUnitID;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception
    {
        super.onClick(client, room, objects);

        if(room != null && client != null && objects.length <= 1)
        {
            /*client.getHabbo().getRoomUnit().cmdTeleport = false;
            this.roomUnitId
            RoomTile loc = room.getLayout().getTile(this.getX(), this.getY());
            if (loc.equals(room.getLayout().getDoorTile()))
                return;

            /*if (!this.getBaseItem().allowWalk())
            {
                loc = HabboItem.getSquareInFront(room.getLayout(), this);
            }

            if (loc != null)
            {

                else
                {
                    client.getHabbo().getRoomUnit().setGoalLocation(loc);
                    Emulator.getThreading().run(new RoomUnitTeleportWalkToAction(client.getHabbo(), this, room), client.getHabbo().getRoomUnit().getPath().size() + 2 * 510);
                }
            }*/

            super.onClick(client, room, objects);

            RoomTile tile = room.getLayout().getTileInFront(room.getLayout().getTile(this.getX(), this.getY()), this.getRotation());
            RoomTile teleportPosition = room.getLayout().getTile(getX(), getY());

            if (tile != null && tile.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                if (!room.hasHabbosAt(this.getX(), this.getY()) && this.roomUnitID == 0)
                {
                    room.sendComposer(new ItemStateComposer2(getId(), 1).compose());
                    room.scheduledTasks.add(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            roomUnitID = client.getHabbo().getRoomUnit().getId();
                            room.updateTile(teleportPosition);
                            client.getHabbo().getRoomUnit().setGoalLocation(room.getLayout().getTile(getX(), getY()));
                        }
                    });
                }
            }
            else if (teleportPosition.equals(client.getHabbo().getRoomUnit().getCurrentLocation()))
            {
                startTeleport(room, client.getHabbo());
            }
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);

        Habbo habbo = room.getHabbo(roomUnit);

        if (habbo != null && habbo.getRoomUnit().getId() == this.roomUnitID)
        {
            if (habbo.getRoomUnit().getGoal().equals(room.getLayout().getTile(this.getX(), this.getY())))
            {
                startTeleport(room, habbo);
            }
        }
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOff(roomUnit, room, objects);
    }

    @Override
    public void run()
    {
        if(!this.getExtradata().equals("0"))
        {
            this.setExtradata("0");

            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            if(room != null)
            {
                room.updateItem(this);
            }
        }
        super.run();
    }

    @Override
    public void onPickUp(Room room)
    {
        this.targetId = 0;
        this.targetRoomId = 0;
        this.setExtradata("0");
    }

    private boolean canUseTeleport(GameClient client, Room room)
    {
        if(!this.getExtradata().equals("0"))
            return false;

        if(client.getHabbo().getRoomUnit().isTeleporting)
            return false;

        if (client.getHabbo().getRoomUnit().getCurrentLocation().is(this.getX(), this.getY()))
            return true;

        return true;
    }

    public int getTargetId()
    {
        return this.targetId;
    }

    public void setTargetId(int targetId)
    {
        this.targetId = targetId;
    }

    public int getTargetRoomId()
    {
        return this.targetRoomId;
    }

    public void setTargetRoomId(int targetRoomId)
    {
        this.targetRoomId = targetRoomId;
    }

    @Override
    public boolean allowWiredResetState()
    {
        return false;
    }

    public void startTeleport(Room room, Habbo habbo)
    {
        if (this.canUseTeleport(habbo.getClient(), room))
        {
            this.roomUnitID = 0;
            habbo.getRoomUnit().isTeleporting = true;
            //new TeleportInteraction(room, client, this).run();
            this.setExtradata("1");
            room.updateItem(this);
            room.scheduledTasks.add(new TeleportActionOne(this, room, habbo.getClient()));
        }
    }

}
