package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.HabboItemNewState;

class TeleportActionThree implements Runnable
{
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionThree(HabboItem currentTeleport, Room room, GameClient client)
    {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run()
    {
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != this.room)
            return;

        HabboItem targetTeleport;
        Room targetRoom = room;

        if(this.currentTeleport.getRoomId() != ((InteractionTeleport)this.currentTeleport).getTargetRoomId())
        {
            targetRoom = Emulator.getGameEnvironment().getRoomManager().loadRoom(((InteractionTeleport) this.currentTeleport).getTargetRoomId());
        }

        if(targetRoom == null)
            return;

        targetTeleport = targetRoom.getHabboItem(((InteractionTeleport) this.currentTeleport).getTargetId());

        if(targetTeleport == null)
            return;

        this.client.getHabbo().getRoomUnit().setLocation(targetRoom.getLayout().getTile(targetTeleport.getX(), targetTeleport.getY()));
        this.client.getHabbo().getRoomUnit().setZ(targetTeleport.getZ());
        this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[targetTeleport.getRotation() % 8]);
        this.client.getHabbo().getRoomUnit().getStatus().remove("mv");

        if(targetRoom != this.room)
        {
            this.room.sendComposer(new RoomUserRemoveComposer(client.getHabbo().getRoomUnit()).compose());
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), targetRoom.getId(), "", Emulator.getConfig().getBoolean("hotel.teleport.locked.allowed"));
        }

        targetTeleport.setExtradata("2");
        targetRoom.updateItem(targetTeleport);
        targetRoom.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());

        this.client.getHabbo().getHabboInfo().setCurrentRoom(targetRoom);
        //Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, this.room, "0"), 500);
        Emulator.getThreading().run(new TeleportActionFour(targetTeleport, targetRoom, this.client), 1000);

    }
}
