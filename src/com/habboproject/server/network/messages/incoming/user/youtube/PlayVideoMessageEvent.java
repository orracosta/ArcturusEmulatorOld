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
import org.apache.commons.lang.StringUtils;


public class PlayVideoMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int itemId = msg.readInt();

        String videoIdStr = msg.readString();
        int videoId = videoIdStr.isEmpty() ? 0 : StringUtils.isNumeric(videoIdStr) ? Integer.parseInt(videoIdStr) : 0;

        RoomItemFloor item = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(itemId);

        PlayerSettings playerSettings;

        playerSettings = PlayerDao.getSettingsById(item.getOwner());

        if (playerSettings == null) {
            playerSettings = client.getPlayer().getSettings();
        }

        if (client.getPlayer().getId() != item.getOwner()) {
            if (item.hasAttribute("video")) {
                for (int i = 0; i < playerSettings.getPlaylist().size(); i++) {
                    if (playerSettings.getPlaylist().get(i).getVideoId().equals(item.getAttribute("video"))) {
                        IPlaylistItem playlistItem = playerSettings.getPlaylist().get(i);

                        //client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new YouTubeDisplayVideoMessageComposer(itemId, playlistItem.getVideoId(), playlistItem.getDuration()));
                        client.send(new YouTubeDisplayVideoMessageComposer(itemId, playlistItem.getVideoId(), playlistItem.getDuration()));
                    }
                }
            }

            return;
        }

        IPlaylistItem playlistItem = playerSettings.getPlaylist().get(videoId);
        client.send(new YoutubeDisplayPlaylistsMessageComposer(itemId, playerSettings.getPlaylist(), videoId));


        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new YouTubeDisplayVideoMessageComposer(itemId, playlistItem.getVideoId(), playlistItem.getDuration()));

        item.setAttribute("video", playlistItem.getVideoId());

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
    }
}
