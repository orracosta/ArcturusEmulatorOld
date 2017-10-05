package com.habboproject.server.network.messages.incoming.room.pets;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.pets.data.PetData;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.rooms.types.tiles.RoomTileState;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.PetInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class PlacePetMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int petId = msg.readInt();
        int x = msg.readInt();
        int y = msg.readInt();

        boolean atDoor = false;

        if (x == 0 && y == 0) {
            x = client.getPlayer().getEntity().getRoom().getModel().getDoorX();
            y = client.getPlayer().getEntity().getRoom().getModel().getDoorY();

            atDoor = true;
        }

        if (client.getPlayer().getEntity() == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        PetData pet = client.getPlayer().getPets().getPet(petId);

        boolean isOwner = client.getPlayer().getId() == room.getData().getOwnerId();

        if(room.getEntities().getPetEntities().size() >= 15) {
            client.send(new AdvancedAlertMessageComposer(Locale.getOrDefault("game.pets.toomany", "There are already too many pets in this room!")));
            return;
        }

        if (isOwner || room.getData().isAllowPets()) {
            if (pet == null) {
                return;
            }

            RoomTile tile = room.getMapping().getTile(x, y);

            if (tile == null) return;

            Position position = new Position(x, y, tile.getWalkHeight());

            if ((!atDoor && tile.getEntities().size() >= 1) || !room.getMapping().isValidPosition(position) || room.getModel().getSquareState()[x][y] != RoomTileState.VALID) {
                return;
            }

            PetEntity petEntity = room.getPets().addPet(pet, position);

            room.getEntities().broadcastMessage(new AvatarsMessageComposer(petEntity));

            for (RoomItemFloor floorItem : room.getItems().getItemsOnSquare(x, y)) {
                floorItem.onEntityStepOn(petEntity);
            }

            tile.getEntities().add(petEntity);

            client.getPlayer().getPets().removePet(pet.getId());
            client.send(new PetInventoryMessageComposer(client.getPlayer().getPets().getPets()));

            if(pet.getTypeId() == 15) {
                client.send(new HorseFigureMessageComposer(petEntity));
            }

            client.getPlayer().getEntity().setPlacedPet(true);
            petEntity.getAI().onAddedToRoom();
        }
    }
}
