package com.habboproject.server.network.messages.incoming.room.pets.horse;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.UnseenItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.items.ItemDao;
import com.google.common.collect.Sets;

public class RemoveHorseSaddleMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        final int petId = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        PetEntity petEntity = room.getEntities().getEntityByPetId(petId);

        if (petEntity == null || petEntity.getData().getOwnerId() != client.getPlayer().getId()) {
            return;
        }

        if (ItemManager.getInstance().getSaddleId() != null) {
            petEntity.getData().setSaddled(false);
            petEntity.getData().saveHorseData();

            room.getEntities().broadcastMessage(new HorseFigureMessageComposer(petEntity));

            long itemId = ItemDao.createItem(client.getPlayer().getId(), ItemManager.getInstance().getSaddleId(), "");

            PlayerItem playerItem = client.getPlayer().getInventory().add(itemId, ItemManager.getInstance().getSaddleId(), 0, "", null, null);
            client.send(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
            client.send(new UpdateInventoryMessageComposer());
        }
    }
}
