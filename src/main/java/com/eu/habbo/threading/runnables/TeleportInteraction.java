package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserEffectComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;

class TeleportInteraction extends Thread
{
    private int state;
    private final Room room;
    private Room targetRoom;
    private final GameClient client;
    private final HabboItem teleportOne;
    private HabboItem teleportTwo;

    @Deprecated
    public TeleportInteraction(Room room, GameClient client, HabboItem teleportOne)
    {
        this.room = room;
        this.client = client;
        this.teleportOne = teleportOne;
        this.teleportTwo = null;
        this.targetRoom = null;
        this.state = 1;
    }

    @Override
    public void run()
    {
        try
        {
            if (state == 5)
            {
                this.teleportTwo.setExtradata("1");
                this.targetRoom.updateItem(this.teleportTwo);
                this.room.updateItem(this.teleportOne);
                RoomTile tile = HabboItem.getSquareInFront(this.room.getLayout(), this.teleportTwo);
                if (tile != null)
                {
                    this.client.getHabbo().getRoomUnit().setGoalLocation(tile);
                }
                Emulator.getThreading().run(this.teleportTwo, 500);
                Emulator.getThreading().run(this.teleportOne, 500);
            } else if (state == 4)
            {
                int[] data = Emulator.getGameEnvironment().getItemManager().getTargetTeleportRoomId(this.teleportOne);
                if (data.length == 2 && data[0] != 0)
                {
                    if (this.room.getId() == data[0])
                    {
                        this.targetRoom = this.room;
                        this.teleportTwo = this.room.getHabboItem(data[1]);

                        if (this.teleportTwo == null)
                        {
                            this.teleportTwo = this.teleportOne;
                        }
                    } else
                    {
                        this.targetRoom = Emulator.getGameEnvironment().getRoomManager().loadRoom(data[0]);
                        this.teleportTwo = this.targetRoom.getHabboItem(data[1]);
                    }
                } else
                {
                    this.targetRoom = this.room;
                    this.teleportTwo = this.teleportOne;
                }

                this.teleportOne.setExtradata("2");
                this.teleportTwo.setExtradata("2");

                if (this.room != this.targetRoom)
                {
                    Emulator.getGameEnvironment().getRoomManager().logExit(this.client.getHabbo());
                    this.room.removeHabbo(this.client.getHabbo());
                    Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), this.targetRoom);
                }

                this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[this.teleportTwo.getRotation()]);
                this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.teleportTwo.getX(), this.teleportTwo.getY()));
                this.client.getHabbo().getRoomUnit().setZ(this.teleportTwo.getZ());

                this.room.sendComposer(new RoomUserRemoveComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.targetRoom.sendComposer(new RoomUserRemoveComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.targetRoom.sendComposer(new RoomUsersComposer(this.client.getHabbo()).compose());
                this.targetRoom.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.targetRoom.sendComposer(new RoomUserEffectComposer(this.client.getHabbo().getRoomUnit()).compose());
                this.room.updateItem(this.teleportOne);
                this.targetRoom.updateItem(this.teleportTwo);

                this.state = 5;

                Emulator.getThreading().run(this, 500);
            } else if (state == 3)
            {
                this.teleportOne.setExtradata("0");
                this.room.updateItem(this.teleportOne);
                this.state = 4;
                Emulator.getThreading().run(this, 500);
            } else if (state == 2)
            {
                this.client.getHabbo().getRoomUnit().setGoalLocation(this.room.getLayout().getTile(this.teleportOne.getX(), this.teleportOne.getY()));
                this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[newRotation(this.teleportOne.getRotation())]);
                this.client.getHabbo().getRoomUnit().getStatus().put("mv", this.teleportOne.getX() + "," + this.teleportOne.getY() + "," + this.teleportOne.getZ());
                //room.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()));

                state = 3;

                Emulator.getThreading().run(this, 500);
            } else if (state == 1)
            {
                RoomTile loc = HabboItem.getSquareInFront(this.room.getLayout(), this.teleportOne);

                if (this.client.getHabbo().getRoomUnit().getX() == loc.x && this.client.getHabbo().getRoomUnit().getY() == loc.y)
                {
                    this.teleportOne.setExtradata("1");
                    this.room.updateItem(this.teleportOne);
                    this.state = 2;

                    Emulator.getThreading().run(this, 250);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int newRotation(int rotation)
    {
        if(rotation == 4)
            return 0;
        if(rotation == 6)
            return 2;
        else
            return rotation + 4;
    }
}
