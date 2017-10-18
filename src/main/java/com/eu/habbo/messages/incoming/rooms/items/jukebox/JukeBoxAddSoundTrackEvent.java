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
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListUpdatedComposer;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxTrackDataComposer;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;

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
                /*for (HabboItem jukeBox : room.getRoomSpecialTypes().getItemsOfType(InteractionJukeBox.class))
                {
                    if (jukeBox instanceof InteractionJukeBox)
                    {
                        SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) item).getSongId());
                        ArrayList<SoundTrack> soundTracks = new ArrayList<SoundTrack>();
                        soundTracks.add(track);

                        if (!((InteractionJukeBox)jukeBox).hasItemInPlaylist(itemId))
                        {
                            ((InteractionJukeBox) jukeBox).addToPlayList(itemId, room);
                        }
                        else
                        {
                            ((InteractionJukeBox) jukeBox).removeFromPlayList(itemId, room);
                        }

                        //room.sendComposer(new JukeBoxPlayListComposer(((InteractionJukeBox) jukeBox), room).compose());
                        THashSet<SoundTrack> soundTrackTHashSet = new THashSet<>();
                        List<Integer> toRemove = new ArrayList<Integer>();
                        ((InteractionJukeBox) jukeBox).getMusicDisks().forEach(new TIntProcedure()
                        {
                            @Override
                            public boolean execute(int i)
                            {
                                HabboItem item = room.getHabboItem(i);

                                if (item != null && item instanceof InteractionMusicDisc)
                                {
                                    SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) item).getSongId());

                                    if (track != null)
                                    {
                                        soundTrackTHashSet.add(track);
                                        return true;
                                    }
                                }
                                toRemove.add(i);

                                return true;
                            }
                        });

                        for (Integer i : toRemove)
                        {
                            ((InteractionJukeBox) jukeBox).removeFromPlayList(i, room);
                        }

                        room.sendComposer(new JukeBoxPlayListUpdatedComposer(soundTrackTHashSet).compose());
                    }
                    break;
                }*/

                if (this.client.getHabbo().getHabboInfo().getCurrentRoom().hasRights(this.client.getHabbo()))
                {
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().getTraxManager().addSong(itemId);
                }
            }
        }
    }
}