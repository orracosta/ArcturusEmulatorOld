package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class JukeBoxPlayListUpdatedComposer extends MessageComposer
{
    private final THashSet<SoundTrack> tracks;

    public JukeBoxPlayListUpdatedComposer(THashSet<SoundTrack> tracks)
    {
        this.tracks = tracks;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxPlayListUpdatedComposer);

        int length = 0;

        for(SoundTrack track : tracks)
        {
            length += track.getLength();
        }

        this.response.appendInt32(length * 1000);
        this.response.appendInt32(this.tracks.size());

        for(SoundTrack track : this.tracks)
        {
            this.response.appendInt32(track.getId());
            this.response.appendInt32(track.getLength() * 1000);
            this.response.appendString(track.getCode());
            this.response.appendString(track.getAuthor());
        }

        return this.response;
    }
}
