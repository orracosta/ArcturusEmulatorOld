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

import java.util.ArrayList;
import java.util.Collection;

public class FloorPlanEditorSaveEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (!this.client.getHabbo().hasPermission("acc_floorplan_editor"))
        {
            this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("floorplan.permission")));
            return;
        }

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

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

            int doorX = this.packet.readInt();
            int doorY = this.packet.readInt();
            int doorRotation = this.packet.readInt();
            int wallSize = this.packet.readInt();
            int floorSize = this.packet.readInt();
            int wallHeight = -1;

            if (doorX < 0 || doorY < 0)
                return;

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

                if (layout.getDoorTile() == null)
                {
                    this.client.sendResponse(new GenericAlertComposer("Error"));
                    ((CustomRoomLayout)layout).needsUpdate(false);
                    Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);
                    return;
                }
                ((CustomRoomLayout)layout).needsUpdate(true);
                Emulator.getThreading().run((CustomRoomLayout)layout);
            }
            else
            {
                layout = Emulator.getGameEnvironment().getRoomManager().insertCustomLayout(room, map, doorX, doorY, doorRotation);
            }

            if(layout != null)
            {
                room.setHasCustomLayout(true);
                room.setNeedsUpdate(true);
                room.setLayout(layout);
                room.setWallSize(wallSize);
                room.setFloorSize(floorSize);
                room.setWallHeight(wallHeight);
                room.save();
                Collection<Habbo> habbos = new ArrayList<Habbo>(room.getUserCount());
                habbos.addAll(room.getHabbos());
                Emulator.getGameEnvironment().getRoomManager().unloadRoom(room);
                room = Emulator.getGameEnvironment().getRoomManager().loadRoom(room.getId());
                ServerMessage message = new ForwardToRoomComposer(room.getId()).compose();
                for (Habbo habbo : habbos)
                {
                    habbo.getClient().sendResponse(message);
                }
            }
        }
    }
}
