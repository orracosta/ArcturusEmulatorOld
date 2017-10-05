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


public class NextVideoMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int itemId = msg.readInt();
        int direction = msg.readInt(); // 0 = previous, 1 = next

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        RoomItemFloor item = client.getPlayer().getEntity().getRoom().getItems().getFloorItem(itemId);

        PlayerSettings playerSettings;

        if (client.getPlayer().getId() != item.getOwner()) {
            return;
        }

        playerSettings = PlayerDao.getSettingsById(item.getOwner());

        if (playerSettings == null) {
            playerSettings = client.getPlayer().getSettings();
        }

        int currentVideoIndex = 0;

        if (item.hasAttribute("video")) {
            String videoAttribute = (String) item.getAttribute("video");
            int playlistSize = playerSettings.getPlaylist().size();

            for (int i = 0; i < playlistSize; i++) {
                if (playerSettings.getPlaylist().get(i).getVideoId().equals(videoAttribute)) {
                    if (direction == 0 && i != 0) {
                        currentVideoIndex = i - 1;
                    } else if (direction == 1 && (playlistSize - 1) > i) {
                        currentVideoIndex = i + 1;
                    }
                }
            }
        }

        if (playerSettings.getPlaylist().size() == 0) return;

        IPlaylistItem playlistItem = playerSettings.getPlaylist().get(currentVideoIndex);
        if (playerSettings.getPlaylist() != null) {
            client.send(new YoutubeDisplayPlaylistsMessageComposer(itemId, playerSettings.getPlaylist(), currentVideoIndex));
        }

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new YouTubeDisplayVideoMessageComposer(itemId, playlistItem.getVideoId(), playlistItem.getDuration()));

        item.setAttribute("video", playlistItem.getVideoId());

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(item));
    }
}
