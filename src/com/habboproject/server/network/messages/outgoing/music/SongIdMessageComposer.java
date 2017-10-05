package com.habboproject.server.network.messages.outgoing.music;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class SongIdMessageComposer extends MessageComposer {

    private String songName;
    private int songId;

    public SongIdMessageComposer(String songName, int songId) {
        this.songName = songName;
        this.songId = songId;
    }

    @Override
    public short getId() {
        return Composers.SongIdMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.songName);
        msg.writeInt(this.songId);
    }
}
