package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.rooms.RoomMoodlightData;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;

/**
 * Created on 14-11-2014 21:11.
 */
public class MoodLightDataComposer extends MessageComposer
{
    private TIntObjectMap<RoomMoodlightData> moodLightData;

    public MoodLightDataComposer(TIntObjectMap<RoomMoodlightData> moodLightData)
    {
        this.moodLightData = moodLightData;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.MoodLightDataComposer);
        this.response.appendInt32(3); //PresetCount

        int index = 1;
        for(RoomMoodlightData data : moodLightData.valueCollection())
        {
            if(data.isEnabled())
            {
                this.response.appendInt32(data.getId());
                index = -1;
                break;
            }
            index++;
        }

        if(index != -1)
        {
            this.response.appendInt32(1);
        }

        int i = 1;
        for(RoomMoodlightData data : moodLightData.valueCollection())
        {
            this.response.appendInt32(data.getId()); //Preset ID
            this.response.appendInt32(data.isBackgroundOnly() ? 2 : 1); //Background only ? 2 : 1
            this.response.appendString(data.getColor()); //Color
            this.response.appendInt32(data.getIntensity()); //Intensity
            i++;
        }

        for(; i <= 3; i++)
        {
            this.response.appendInt32(i);
            this.response.appendInt32(1);
            this.response.appendString("#000000");
            this.response.appendInt32(255);
        }

        /*for(int i = moodLightData.valueCollection().size(); i < 3; i++)
        {
            this.response.appendInt32(i);
            this.response.appendInt32(1);
            this.response.appendString("#000000");
            this.response.appendInt32(255);
        }*/
        //:test 2780 i:1 i:1 i:1 i:2 s:#FF00FF i:255
        return this.response;
    }
}
