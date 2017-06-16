package com.eu.habbo.messages.outgoing.rooms.items.jukebox;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class JukeBoxPlayListComposer extends MessageComposer
{
    private final InteractionJukeBox jukeBox;
    private final Room room;

    public JukeBoxPlayListComposer(InteractionJukeBox jukeBox, Room room)
    {
        this.jukeBox = jukeBox;
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.JukeBoxPlayListComposer);

        ArrayList<SoundTrack> soundTracks = new ArrayList<SoundTrack>();

        long totalTime = 0;
        for (Integer i : this.jukeBox.getMusicDisks().toArray())
        {
            HabboItem item = room.getHabboItem(i);

            if (item != null && item instanceof InteractionMusicDisc)
            {
                SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) item).getSongId());

                if (track != null)
                {
                    soundTracks.add(track);
                    totalTime += (track.getLength() * 1000);
                }
            }
        }
        this.response.appendInt((int) totalTime); //Dunno //TODO Total play length?
        this.response.appendInt(soundTracks.size());

        for (SoundTrack soundTrack : soundTracks)
        {
            this.response.appendInt(soundTrack.getId());
            this.response.appendInt(soundTrack.getLength() * 1000);
        }

        return this.response;
    }
}
