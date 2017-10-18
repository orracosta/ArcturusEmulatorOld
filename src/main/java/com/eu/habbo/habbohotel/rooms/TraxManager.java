package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Disposable;
import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TraxManager implements Disposable
{
    private final Room room;
    private final List<InteractionMusicDisc> songs = new ArrayList<>(0);
    private int totalLength = 0;
    private int startedTimestamp;
    private InteractionMusicDisc currentlyPlaying = null;
    private int playingIndex = 0;

    private boolean disposed = false;

    public TraxManager(Room room)
    {
        this.room = room;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_trax_playlist WHERE room_id = ?"))
        {
            statement.setInt(1, this.room.getId());
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    HabboItem musicDisc = this.room.getHabboItem(set.getInt("item_id"));
                    if (musicDisc != null && musicDisc instanceof InteractionMusicDisc)
                    {
                        SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) musicDisc).getSongId());

                        if (track != null)
                        {
                            this.songs.add((InteractionMusicDisc) musicDisc);
                            this.totalLength += track.getLength();
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void cycle()
    {
        if (this.isPlaying())
        {
            if (timePlaying() >= this.totalLength)
            {
                this.play(0);
                //restart
            }

            if (Emulator.getIntUnixTimestamp() >= this.startedTimestamp + this.currentSong().getLength())
            {
                this.play((this.playingIndex + 1) % this.songs.size());
            }
        }
    }

    public void play(int index)
    {
        if (this.currentlyPlaying == null)
        {
            for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionJukeBox.class))
            {
                item.setExtradata("1");
                this.room.updateItem(item);
            }
        }

        if (!this.songs.isEmpty())
        {
            index = index % this.songs.size();

            this.currentlyPlaying = this.songs.get(index);

            if (this.currentlyPlaying != null)
            {
                this.room.setJukeBoxActive(true);
                this.startedTimestamp = Emulator.getIntUnixTimestamp();
                this.playingIndex = index;
            }

            this.room.sendComposer(new JukeBoxNowPlayingMessageComposer(Emulator.getGameEnvironment().getItemManager().getSoundTrack(this.currentlyPlaying.getSongId()), this.playingIndex, 0).compose());
        }
        else
        {
            this.stop();
        }
    }

    public void stop()
    {
        this.room.setJukeBoxActive(false);
        this.currentlyPlaying = null;
        this.startedTimestamp = 0;
        this.playingIndex = 0;

        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionJukeBox.class))
        {
            item.setExtradata("0");
            this.room.updateItem(item);
        }

        this.room.sendComposer(new JukeBoxNowPlayingMessageComposer(null, -1, 0).compose());
    }

    public SoundTrack currentSong()
    {
        return Emulator.getGameEnvironment().getItemManager().getSoundTrack(this.songs.get(this.playingIndex).getSongId());
    }

    public void addSong(int itemId)
    {
        HabboItem musicDisc = this.room.getHabboItem(itemId);

        if (musicDisc != null && musicDisc instanceof InteractionMusicDisc)
        {
            SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(((InteractionMusicDisc) musicDisc).getSongId());

            if (track != null)
            {
                this.totalLength += track.getLength();
                this.songs.add((InteractionMusicDisc) musicDisc);

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_trax_playlist (room_id, item_id) VALUES (?, ?)"))
                {
                    statement.setInt(1, this.room.getId());
                    statement.setInt(2, itemId);
                    statement.execute();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                    return;
                }

                this.room.sendComposer(new JukeBoxPlayListComposer(this.songs, this.totalLength).compose());
                this.room.sendComposer(new JukeBoxMySongsComposer(this.myList()).compose());
            }
        }
    }

    public void removeSong(int itemId)
    {
        InteractionMusicDisc musicDisc = this.fromItemId(itemId);

        if (musicDisc != null)
        {
            this.songs.remove(musicDisc);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_trax_playlist WHERE room_id = ? AND item_id = ? LIMIT 1"))
            {
                statement.setInt(1, this.room.getId());
                statement.setInt(2, itemId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
                return;
            }

            this.totalLength -= Emulator.getGameEnvironment().getItemManager().getSoundTrack(musicDisc.getSongId()).getLength();
            if (this.currentlyPlaying == musicDisc)
            {
                this.play(this.playingIndex);
            }

            this.room.sendComposer(new JukeBoxPlayListComposer(this.songs, this.totalLength).compose());
        }
        this.room.sendComposer(new JukeBoxMySongsComposer(this.myList()).compose());
    }

    public int timePlaying()
    {
        return Emulator.getIntUnixTimestamp() - this.startedTimestamp;
    }

    public int totalLength()
    {
        return totalLength;
    }

    public List<InteractionMusicDisc> getSongs()
    {
        return songs;
    }

    public boolean isPlaying()
    {
        return this.currentlyPlaying != null;
    }

    public List<SoundTrack> soundTrackList()
    {
        List<SoundTrack> trax = new ArrayList<>(this.songs.size());

        for (InteractionMusicDisc musicDisc : this.songs)
        {
            SoundTrack track = Emulator.getGameEnvironment().getItemManager().getSoundTrack(musicDisc.getSongId());

            if (track != null)
            {
                trax.add(track);
            }
        }

        return trax;
    }

    public List<InteractionMusicDisc> myList()
    {
        List<InteractionMusicDisc> trax = new ArrayList<>();

        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionMusicDisc.class))
        {
            if (!this.songs.contains(item))
            {
                trax.add((InteractionMusicDisc) item);
            }
        }

        return trax;
    }

    public InteractionMusicDisc fromItemId(int itemId)
    {
        for (InteractionMusicDisc musicDisc : this.songs)
        {
            if (musicDisc.getId() == itemId)
            {
                return musicDisc;
            }
        }

        return null;
    }

    public void clearPlayList()
    {
        this.songs.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_trax_playlist WHERE room_id = ?"))
        {
            statement.setInt(1, this.room.getId());
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void updateCurrentPlayingSong(Habbo habbo)
    {
        if (this.isPlaying())
        {
            habbo.getClient().sendResponse(new JukeBoxNowPlayingMessageComposer(Emulator.getGameEnvironment().getItemManager().getSoundTrack(this.currentlyPlaying.getSongId()), this.playingIndex, 1000 * (Emulator.getIntUnixTimestamp() - this.startedTimestamp)));
        }
        else
        {
            habbo.getClient().sendResponse(new JukeBoxNowPlayingMessageComposer(null, -1, 0));
        }
    }

    @Override
    public void dispose()
    {
        this.disposed = true;
    }

    @Override
    public boolean disposed()
    {
        return this.disposed;
    }
}
