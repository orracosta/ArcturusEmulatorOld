package com.habboproject.server.game.rooms.objects.items.types.floor.jukebox;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.music.MusicData;
import com.habboproject.server.game.items.music.SongItemData;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.messages.outgoing.music.PlayMusicMessageComposer;
import com.habboproject.server.utilities.JsonFactory;
import com.habboproject.server.utilities.attributes.Stateable;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SoundMachineFloorItem extends RoomItemFloor implements Stateable {
    public static final int MAX_CAPACITY = 20;

    private boolean isPlaying = false;
    private int currentPlayingIndex = -1;
    private long startTimestamp = 0;

    private List<SongItemData> songs;

    private boolean pendingPlay = false;

    public SoundMachineFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (data.startsWith("##jukeboxOn[")) {
            this.songs = JsonFactory.getInstance().fromJson(data.replace("##jukeboxOn", ""), new TypeToken<ArrayList<SongItemData>>() {
            }.getType());

            this.pendingPlay = true;
            this.setTicks(RoomItemFactory.getProcessTime(1.5));
        } else if (data.startsWith("##jukeboxOff[")) {
            this.songs = JsonFactory.getInstance().fromJson(data.replace("##jukeboxOff", ""), new TypeToken<ArrayList<SongItemData>>() {
            }.getType());
        } else if (data.startsWith("[")) {
            this.songs = JsonFactory.getInstance().fromJson(data, new TypeToken<ArrayList<SongItemData>>() {
            }.getType());
        } else {
            this.songs = new ArrayList<>();
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity instanceof PlayerEntity) {
            if (((PlayerEntity) entity).getPlayerId() != this.getRoom().getData().getOwnerId() && !((PlayerEntity) entity).getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        if (requestData == -1) return false;

        if (this.isPlaying) {
            this.stop();
        } else {
            this.play();
        }

        this.sendUpdate();
        return true;
    }

    @Override
    public void onTickComplete() {
        if (this.pendingPlay) {
            this.play();
            this.pendingPlay = false;
            this.sendUpdate();
            return;
        }

        if (this.isPlaying) {
            if (this.currentPlayingIndex >= this.getSongs().size()) {
                this.stop();
                this.sendUpdate();
                return;
            }

            for (PlayerEntity entity : this.getRoom().getEntities().getPlayerEntities()) {
                if (!entity.hasAttribute("traxSent") && (System.currentTimeMillis() - entity.getJoinTime() >= 1100)) {
                    entity.getPlayer().getSession().send(this.getComposer());
                    entity.setAttribute("traxSent", true);
                }
            }

            if (this.currentPlayingIndex != -1) {
                SongItemData songItemData = this.getSongs().get(this.currentPlayingIndex);

                if (songItemData != null) {
                    MusicData musicData = ItemManager.getInstance().getMusicData(songItemData.getSongId());

                    if (musicData != null) {
                        if (this.timePlaying() >= (musicData.getLengthSeconds() + 1.0)) {
                            this.playNextSong();
                        }
                    }
                }
            }

            this.setTicks(RoomItemFactory.getProcessTime(1.0));
        }
    }

    @Override
    public void onPickup() {
        this.setExtraData(this.getDataObject());
    }

    @Override
    public void onPlaced() {
        if (this.isPlaying) {
            this.broadcastSong();
        }
    }

    public void addSong(SongItemData songItemData) {
        this.songs.add(songItemData);
    }

    public SongItemData removeSong(int index) {
        if (index == this.currentPlayingIndex) {
            this.playNextSong();
        }

        SongItemData songItemData = this.songs.get(index);
        this.songs.remove(index);

        return songItemData;
    }

    public void play() {
        if (this.songs.size() == 0) return;

        this.isPlaying = true;
        this.currentPlayingIndex = -1;

        this.playNextSong();
        this.setTicks(RoomItemFactory.getProcessTime(1.0));
    }

    public void stop() {
        this.isPlaying = false;
        this.currentPlayingIndex = -1;

        this.broadcastSong();
    }

    public void playNextSong() {
        this.startTimestamp = Comet.getTime();
        this.currentPlayingIndex++;

        if (currentPlayingIndex >= this.getSongs().size()) {
            currentPlayingIndex = -1;
        }

        this.broadcastSong();
    }

    public void broadcastSong() {
        if (!this.isPlaying || this.currentPlayingIndex >= this.songs.size()) {
            this.getRoom().getEntities().broadcastMessage(new PlayMusicMessageComposer());

            if (this.isPlaying)
                this.isPlaying = false;
            return;
        }

        for (PlayerEntity entity : this.getRoom().getEntities().getPlayerEntities()) {
            if (!entity.hasAttribute("traxSent")) {
                entity.setAttribute("traxSent", true);
            }

            if (entity.getPlayer() != null && entity.getPlayer().getSession() != null)
                entity.getPlayer().getSession().send(this.getComposer());
        }
    }

    public MessageComposer getComposer() {
        if (this.currentPlayingIndex == -1) return null;

        SongItemData songItemData = this.songs.get(this.currentPlayingIndex);

        if (songItemData == null) {
            return null;
        }

        int songId = songItemData.getSongId();
        return new PlayMusicMessageComposer(songId, this.currentPlayingIndex, this.songTimeSync());
    }

    private int timePlaying() {
        return (int) (Comet.getTime() - this.startTimestamp);
    }

    public int songTimeSync() {
        if (!this.isPlaying || this.currentPlayingIndex < 0 || this.currentPlayingIndex >= this.getSongs().size()) {
            return 0;
        }

        SongItemData songItemData = this.getSongs().get(this.currentPlayingIndex);

        if (songItemData != null) {
            MusicData musicData = ItemManager.getInstance().getMusicData(songItemData.getSongId());

            if (musicData != null) {
                if (this.timePlaying() >= musicData.getLengthSeconds())
                    return musicData.getLengthSeconds();
            }
        }

        return this.timePlaying() * 1000;
    }

    @Override
    public String getDataObject() {
        return (this.isPlaying ? "##jukeboxOn" : "##jukeboxOff") + JsonFactory.getInstance().toJson(this.songs);
    }

    @Override
    public void onUnload() {
        this.getSongs().clear();
    }

    public List<SongItemData> getSongs() {
        return this.songs;
    }

    @Override
    public boolean getState() {
        return this.isPlaying;
    }
}
