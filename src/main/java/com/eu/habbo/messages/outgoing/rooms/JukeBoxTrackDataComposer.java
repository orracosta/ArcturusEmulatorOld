package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;

/**
 * Created on 25-8-2015 15:41.
 */
public class JukeBoxTrackDataComposer extends MessageComposer
{
    private List<SoundTrack> tracks;

    public JukeBoxTrackDataComposer(List<SoundTrack> tracks)
    {
        this.tracks = tracks;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxTrackDataComposer);
        this.response.appendInt32(this.tracks.size());

        for(SoundTrack track : this.tracks)
        {
            this.response.appendInt32(track.getId());
            this.response.appendString(track.getCode());
            this.response.appendString(track.getName());
            this.response.appendString(track.getData());
            this.response.appendInt32(track.getLength() * 1000);
            this.response.appendString(track.getAuthor());
        }

        return this.response;
    }
}
