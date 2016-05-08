package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.CanCreateRoomComposer;
import com.eu.habbo.messages.outgoing.navigator.RoomCreatedComposer;

/**
 * Created on 31-8-2014 15:05.
 */
public class RequestCreateRoomEvent extends MessageHandler {

    @Override
    public void handle() throws Exception
    {
        String name = this.packet.readString();
        String description = this.packet.readString();
        String modelName = this.packet.readString();
        int categoryId = this.packet.readInt();
        int maxUsers = this.packet.readInt();
        int tradeType = this.packet.readInt();

        RoomLayout layout = Emulator.getGameEnvironment().getRoomManager().getLayout(modelName);

        if(layout == null)
        {
            Emulator.getLogging().logErrorLine("[SCRIPTER] Incorrect layout name \""+modelName+"\". " + this.client.getHabbo().getHabboInfo().getUsername());
            return;
        }

        RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategory(categoryId);

        if(category == null || category.getMinRank() > this.client.getHabbo().getHabboInfo().getRank())
        {
            Emulator.getLogging().logErrorLine("[SCRIPTER] Incorrect rank or non existing category ID: \""+categoryId+"\"." + this.client.getHabbo().getHabboInfo().getUsername());
            return;
        }

        if(maxUsers > 250)
            return;

        if(tradeType > 2)
            return;

        int currentRoomCount = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo()).size();
        if(currentRoomCount >= Emulator.getConfig().getInt("hotel.max.rooms.vip") && this.client.getHabbo().getHabboStats().hasActiveClub())
        {
            this.client.sendResponse(new CanCreateRoomComposer(currentRoomCount));
            return;
        }
        else if(currentRoomCount >= Emulator.getConfig().getInt("hotel.max.rooms.user"))
        {
            this.client.sendResponse(new CanCreateRoomComposer(currentRoomCount));
            return;
        }

        Room room = Emulator.getGameEnvironment().getRoomManager().createRoom(this.client.getHabbo(), name, description, modelName, maxUsers, categoryId);

        if(room != null)
        {
            this.client.sendResponse(new RoomCreatedComposer(room));
        }
    }
}
