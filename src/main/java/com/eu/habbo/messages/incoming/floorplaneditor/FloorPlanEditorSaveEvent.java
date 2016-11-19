package com.eu.habbo.messages.incoming.floorplaneditor;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.CustomRoomLayout;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import gnu.trove.set.hash.THashSet;

public class FloorPlanEditorSaveEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        final Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room == null)
            return;

        if(room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner"))
        {
            String map = this.packet.readString();

            if(map.isEmpty() || map.length() == 0)
                return;

            int lengthX = -1;

            String[] data = map.split(((char)13) + "");

            if(data.length > 64)
            {
                this.client.sendResponse(new GenericAlertComposer("Incorrect Floorplan! Maximum size of 64 x 64"));
                return;
            }

            for(String s : data)
            {
                if(lengthX == -1)
                {
                    lengthX = s.length();
                }

                if(s.length() != lengthX || s.length() > 50)
                {
                    this.client.sendResponse(new GenericAlertComposer("Incorrect Floorplan! Maximum size of 64 x 64"));
                    return;
                }
            }

            map = map.substring(0, map.length()-1).replace((char)13 +"", "\r\n");

            int doorX = this.packet.readInt();
            int doorY = this.packet.readInt();
            int doorRotation = this.packet.readInt();
            int wallSize = this.packet.readInt();
            int floorSize = this.packet.readInt();
            int wallHeight = -1;

            if(this.packet.bytesAvailable() >= 4)
                wallHeight = this.packet.readInt();

            RoomLayout layout = room.getLayout();

            if(layout instanceof CustomRoomLayout)
            {
                layout.setDoorX((short) doorX);
                layout.setDoorY((short) doorY);
                layout.setDoorDirection(doorRotation);
                layout.setHeightmap(map);
                layout.parse();
                ((CustomRoomLayout)layout).needsUpdate(true);
                Emulator.getThreading().run((CustomRoomLayout)layout);
            }
            else
            {
                layout = Emulator.getGameEnvironment().getRoomManager().insertCustomLayout(room, map, doorX, doorY, doorRotation);
            }

            if(layout != null)
            {
                final THashSet<Habbo> habbos = new THashSet<Habbo>(room.getCurrentHabbos().valueCollection());

                room.setHasCustomLayout(true);
                room.setNeedsUpdate(true);
                room.setLayout(layout);
                room.setWallSize(wallSize);
                room.setFloorSize(floorSize);
                room.setWallHeight(wallHeight);

                Emulator.getThreading().run(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        room.dispose();

                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException e)
                        {
                        }

                        ServerMessage message = new ForwardToRoomComposer(room.getId()).compose();
                        for(Habbo habbo : habbos)
                        {
                            habbo.getClient().sendResponse(message);
                        }
                    }
                });
            }
            else
            {
                this.client.sendResponse(new GenericAlertComposer("Oh boi. Saving Failed! Please try again in a few minutes!"));
            }
        }
    }
}
