package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 5-9-2015 10:34.
 */
public class JukeBoxPlayListComposer extends MessageComposer
{
    private final THashSet<SoundTrack> tracks;

    public JukeBoxPlayListComposer(THashSet<SoundTrack> tracks)
    {
        this.tracks = tracks;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxPlayListComposer);
        this.response.appendInt32(100); //Dunno //TODO Total play length?
        this.response.appendInt32(this.tracks.size());

        for(SoundTrack track : tracks)
        {
            this.response.appendInt32(track.getId());
            this.response.appendInt32(track.getLength() * 1000);
        }

        return this.response;
    }
}
