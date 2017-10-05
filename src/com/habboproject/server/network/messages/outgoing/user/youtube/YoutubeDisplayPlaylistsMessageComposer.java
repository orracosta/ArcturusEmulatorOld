package com.habboproject.server.network.messages.outgoing.user.youtube;

import com.habboproject.server.api.game.players.data.types.IPlaylistItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.List;


public class YoutubeDisplayPlaylistsMessageComposer extends MessageComposer {
    private final int itemId;
    private final List<IPlaylistItem> playlist;
    private final int videoId;

    public YoutubeDisplayPlaylistsMessageComposer(final int itemId, final List<IPlaylistItem> playlist, final int videoId) {
        this.itemId = itemId;
        this.playlist = playlist;
        this.videoId = videoId;
    }

    @Override
    public short getId() {
        return Composers.YoutubeDisplayPlaylistsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(itemId);

        msg.writeInt(playlist.size());

        for (IPlaylistItem playListItem : playlist) {
            msg.writeString(playlist.indexOf(playListItem)); // not sure if can do this...
            msg.writeString(playListItem.getTitle());
            msg.writeString(playListItem.getDescription());
        }

        msg.writeString(videoId);
    }
}
