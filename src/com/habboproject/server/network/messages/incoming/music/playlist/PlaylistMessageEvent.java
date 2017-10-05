package com.habboproject.server.network.messages.incoming.music.playlist;

import com.habboproject.server.game.rooms.objects.items.types.floor.jukebox.SoundMachineFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.music.playlist.PlaylistMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class PlaylistMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        if (client.getPlayer().getId() != room.getData().getOwnerId() && !client.getPlayer().getPermissions().getRank().roomFullControl())
            return;

        SoundMachineFloorItem soundMachineFloorItem = room.getItems().getSoundMachine();

        if (soundMachineFloorItem == null) {
            return;
        }

        client.send(new PlaylistMessageComposer(soundMachineFloorItem.getSongs()));
    }
}
