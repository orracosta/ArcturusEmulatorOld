package com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;

/**
 * Created by brend on 02/02/2017.
 */
public class WiredAddonPuzzleBoxFloorItem extends RoomItemFloor {
    public WiredAddonPuzzleBoxFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity == null || !(entity instanceof PlayerEntity)) {
            return false;
        }

        if (this.getPosition().distanceTo(entity.getPosition()) >= 0.0 && this.getPosition().distanceTo(entity.getPosition()) <= 1.0) {
            entity.setBodyRotation(Position.calculateRotation(entity.getPosition().getX(), entity.getPosition().getY(), this.getPosition().getX(), this.getPosition().getY(), false));
            entity.setHeadRotation(entity.getBodyRotation());

            if (entity.getBodyRotation() % 2 != 0) {
                entity.moveTo(this.getPosition().getX() + 1, this.getPosition().getY());
                return true;
            }

            Position oldPosition = this.getPosition().copy();
            Position newPosition = new Position(0, 0, this.getPosition().getZ());

            switch (entity.getBodyRotation()) {
                case 0: {
                    newPosition = new Position(this.getPosition().getX(), this.getPosition().getY() - 1);
                    break;
                }
                case 2: {
                    newPosition = new Position(this.getPosition().getX() + 1, this.getPosition().getY());
                    break;
                }
                case 4: {
                    newPosition = new Position(this.getPosition().getX(), this.getPosition().getY() + 1);
                    break;
                }
                case 6: {
                    newPosition = new Position(this.getPosition().getX() - 1, this.getPosition().getY());
                }
            }

            if (this.getRoom().getItems().moveFloorItem(this.getId(), newPosition, this.getRotation(), true) && oldPosition != null) {
                this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(oldPosition, newPosition, 0, 0, this.getVirtualId()));
                entity.moveTo(oldPosition.getX(), oldPosition.getY());
            }
        } else {
            entity.moveTo(this.getPosition().getX() + 1, this.getPosition().getY());
        }

        return true;
    }
}
