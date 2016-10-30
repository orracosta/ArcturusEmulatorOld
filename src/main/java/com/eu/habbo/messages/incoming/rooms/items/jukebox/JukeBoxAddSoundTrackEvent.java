package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListAddSongComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxTrackDataComposer;

import java.util.ArrayList;

public class JukeBoxAddSoundTrackEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        int unknown = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null)
        {
            HabboItem item = room.getHabboItem(itemId);

            if (item instanceof InteractionMusicDisc)
            {
                for (HabboItem jukeBox : room.getRoomSpecialTypes().getItemsOfType(InteractionJukeBox.class))
                {
                    if (jukeBox instanceof InteractionJukeBox)
                    {
                        if (!((InteractionJukeBox)jukeBox).hasItemInPlaylist(itemId))
                        {
                            ((InteractionJukeBox) jukeBox).addToPlayList(itemId, room);
                        }
                        else
                        {
                            ((InteractionJukeBox) jukeBox).removeFromPlayList(itemId, room);
                        }
                        ArrayList<SoundTrack> soundTracks = new ArrayList<SoundTrack>();
                        soundTracks.add(Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) item).getSongId()));

                        //room.sendComposer(new JukeBoxPlayListAddSongComposer(Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) item).getSongId())).compose());
                        room.sendComposer(new JukeBoxPlayListComposer(((InteractionJukeBox) jukeBox), room).compose());
                    }
                    break;
                }
            }
        }
    }
}