package com.habboproject.server.network.messages.incoming.music;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.music.MusicData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.music.SongIdMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class SongIdMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String songName = msg.readString();

        MusicData musicData = ItemManager.getInstance().getMusicDataByName(songName);

        if (musicData != null) {
            client.send(new SongIdMessageComposer(musicData.getName(), musicData.getSongId()));
        }
    }
}