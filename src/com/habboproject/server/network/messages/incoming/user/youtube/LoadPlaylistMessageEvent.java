package com.habboproject.server.network.messages.incoming.user.youtube;

import com.habboproject.server.api.game.players.data.types.IPlaylistItem;
import com.habboproject.server.game.players.types.PlayerSettings;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.youtube.YouTubeDisplayVideoMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.youtube.YoutubeDisplayPlaylistsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;

import java.util.List;


public class LoadPlaylistMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int itemId = msg.readInt();

        RoomItemFloor item = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(itemId);

        if (item == null)
            return;

        PlayerSettings playerSettings;

        //if(client.getPlayer().getId() != item.getOwner()) {
        //    return;
        //}

        playerSettings = PlayerDao.getSettingsById(item.getOwner());

        if (playerSettings == null) {
            playerSettings = client.getPlayer().getSettings();
        }

        int playingId = 0;

        if (item.hasAttribute("video")) {
            for (int i = 0; i < playerSettings.getPlaylist().size(); i++) {
                if (playerSettings.getPlaylist().get(i).getVideoId().equals(item.getAttribute("video"))) {
                    playingId = i;
                }
            }
        }

        List<IPlaylistItem> playlist = playerSettings.getPlaylist();
        if (playlist != null) {
            client.send(new YoutubeDisplayPlaylistsMessageComposer(itemId, playlist, playingId));

            if (playlist.size() > 0) {
                IPlaylistItem video = playlist.get(playingId);
                client.send(new YouTubeDisplayVideoMessageComposer(itemId, video.getVideoId(), video.getDuration()));

                item.setAttribute("video", video.getVideoId());

                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
            }
        }
    }
}
