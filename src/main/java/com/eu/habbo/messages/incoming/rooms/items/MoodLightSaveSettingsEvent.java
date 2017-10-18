package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionMoodLight;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomMoodlightData;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.MoodLightDataComposer;

public class MoodLightSaveSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if((room.getGuildId() > 0 && room.guildRightLevel(this.client.getHabbo()) < 2) && !room.hasRights(this.client.getHabbo()))
            return;

        int id = this.packet.readInt();
        int backgroundOnly = this.packet.readInt();
        String color = this.packet.readString();
        int intensity = this.packet.readInt();

        for(RoomMoodlightData data : room.getMoodlightData().valueCollection())
        {
            if(data.getId() == id)
            {
                data.setBackgroundOnly(backgroundOnly == 2);
                data.setColor(color);
                data.setIntensity(intensity);
                data.enable();

                for(HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionMoodLight.class))
                {
                    item.setExtradata(data.toString());
                    item.needsUpdate(true);
                    room.updateItem(item);
                    Emulator.getThreading().run(item);
                }
            }
            else
            {
                data.disable();
            }
        }

        room.setNeedsUpdate(true);
        this.client.sendResponse(new MoodLightDataComposer(room.getMoodlightData()));
    }
}
