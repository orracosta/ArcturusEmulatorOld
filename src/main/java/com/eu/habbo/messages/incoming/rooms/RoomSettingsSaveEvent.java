package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.rooms.RoomState;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.*;

/**
 * Created on 22-10-2014 16:38.
 */
public class RoomSettingsSaveEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int roomId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room.getId() == roomId)
        {
            if(room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner"))
            {
                room.setName(this.packet.readString());
                room.setDescription(this.packet.readString());
                room.setState(RoomState.values()[this.packet.readInt() % RoomState.values().length]);
                room.setPassword(this.packet.readString());
                room.setUsersMax(this.packet.readInt());

                int categoryId = this.packet.readInt();

                if(Emulator.getGameEnvironment().getRoomManager().hasCategory(categoryId, this.client.getHabbo()))
                    room.setCategory(categoryId);
                else
                {
                    RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId);

                    String message = "";

                    if(category == null)
                    {
                        message = Emulator.getTexts().getValue("scripter.warning.roomsettings.category.nonexisting").replace("%username%", client.getHabbo().getHabboInfo().getUsername());
                    }
                    else
                    {
                        message = Emulator.getTexts().getValue("scripter.warning.roomsettings.category.permission").replace("%username%", client.getHabbo().getHabboInfo().getUsername()).replace("%category%", Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId) + "");
                    }

                    Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", message);
                    Emulator.getLogging().logUserLine(message);
                }

                String tags = "";
                int count = this.packet.readInt();
                for(int i = 0; i < count; i++)
                {
                    tags += this.packet.readString() + ";";
                }

                room.setTags(tags);
                this.packet.readInt(); //Trade Mode
                room.setAllowPets(this.packet.readBoolean());
                room.setAllowPetsEat(this.packet.readBoolean());
                room.setAllowWalkthrough(this.packet.readBoolean());
                room.setHideWall(this.packet.readBoolean());
                room.setWallSize(this.packet.readInt());
                room.setFloorSize(this.packet.readInt());
                room.setMuteOption(this.packet.readInt());
                room.setKickOption(this.packet.readInt());
                room.setBanOption(this.packet.readInt());
                room.setChatMode(this.packet.readInt());
                room.setChatWeight(this.packet.readInt());
                room.setChatSpeed(this.packet.readInt());
                room.setChatDistance(Math.abs(this.packet.readInt()));
                room.setChatProtection(this.packet.readInt());
                room.setNeedsUpdate(true);

                room.sendComposer(new RoomThicknessComposer(room).compose());
                room.sendComposer(new RoomChatSettingsComposer(room).compose());
                room.sendComposer(new RoomSettingsUpdatedComposer(room).compose());
                this.client.sendResponse(new RoomSettingsSavedComposer(room));
                //TODO Find packet for update room name.
            }
        }
    }
}
