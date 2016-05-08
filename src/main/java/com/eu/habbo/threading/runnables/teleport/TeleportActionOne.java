package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.HabboItemNewState;

/**
 * Created on 7-3-2015 22:01.
 */
public class TeleportActionOne implements Runnable
{
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionOne(HabboItem currentTeleport, Room room, GameClient client)
    {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run()
    {
        this.client.getHabbo().getRoomUnit().setGoalLocation(currentTeleport.getX(), currentTeleport.getY());
        this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[(this.currentTeleport.getRotation() + 4) % 8]);
        this.client.getHabbo().getRoomUnit().getStatus().put("mv", this.currentTeleport.getX() + "," + this.currentTeleport.getY() + "," + this.currentTeleport.getZ());
        this.room.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());
        this.client.getHabbo().getRoomUnit().setX(this.currentTeleport.getX());
        this.client.getHabbo().getRoomUnit().setY(this.currentTeleport.getY());
        this.client.getHabbo().getRoomUnit().setZ(this.currentTeleport.getZ());
        this.client.getHabbo().getRoomUnit().getStatus().remove("mv");
        this.currentTeleport.setExtradata("0");

        Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, room, "0"), 500);
        Emulator.getThreading().run(new TeleportActionTwo(currentTeleport, room, client), 1000);
    }
}
