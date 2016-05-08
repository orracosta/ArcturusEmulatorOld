package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 5-9-2015 11:05.
 */
public class JukeBoxNowPlayingMessageComposer extends MessageComposer
{
    private final SoundTrack track;
    private final int playListId;
    private final int msPlayed;

    public JukeBoxNowPlayingMessageComposer(SoundTrack track, int playListId, int msPlayed)
    {
        this.track = track;
        this.playListId = playListId;
        this.msPlayed = msPlayed;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxNowPlayingMessageComposer);

        if(this.track != null)
        {
            this.response.appendInt32(this.track.getId());
            this.response.appendInt32(this.playListId);
            this.response.appendInt32(this.track.getId());
            this.response.appendInt32(this.track.getLength());
            this.response.appendInt32(this.msPlayed);
        }
        else
        {
            this.response.appendInt32(-1);
            this.response.appendInt32(-1);
            this.response.appendInt32(-1);
            this.response.appendInt32(-1);
            this.response.appendInt32(-1);
        }
        return this.response;
    }
}
