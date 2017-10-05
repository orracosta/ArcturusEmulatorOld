package com.habboproject.server.network.messages.incoming.music.playlist;

import com.habboproject.server.game.items.music.SongItemData;
import com.habboproject.server.game.rooms.objects.items.types.floor.jukebox.SoundMachineFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.music.playlist.PlaylistMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.SongInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;

public class PlaylistRemoveMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int songIndex = msg.readInt();

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

        if (songIndex < 0 || songIndex >= soundMachineFloorItem.getSongs().size()) {
            return;
        }

        SongItemData songItemData = soundMachineFloorItem.removeSong(songIndex);
        soundMachineFloorItem.saveData();

        RoomItemDao.removeItemFromRoom(songItemData.getItemSnapshot().getId(), client.getPlayer().getId());
        client.getPlayer().getInventory().add(songItemData.getItemSnapshot().getId(), songItemData.getItemSnapshot().getBaseItemId(), 0, songItemData.getItemSnapshot().getExtraData(), null);

        client.send(new UpdateInventoryMessageComposer());

        client.send(new SongInventoryMessageComposer(client.getPlayer().getInventory().getSongs()));
        client.send(new PlaylistMessageComposer(soundMachineFloorItem.getSongs()));
    }
}
