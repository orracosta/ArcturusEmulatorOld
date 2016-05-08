package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class JukeBoxTrackCodeComposer extends MessageComposer
{
    private final SoundTrack track;

    public JukeBoxTrackCodeComposer(SoundTrack track)
    {
        this.track = track;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxTrackCodeComposer);
        this.response.appendString(track.getCode());
        this.response.appendInt32(track.getId());
        return this.response;
    }
}
