package com.habboproject.server.threads.executors.wired.actions;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.DiceFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.threads.CometThread;

import java.util.List;
import java.util.Random;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionMoveRotateExecuteEvent implements CometThread {
    private static final int PARAM_MOVEMENT = 0;
    private static final int PARAM_ROTATION = 1;

    private final Random random = new Random();

    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;

    public ActionMoveRotateExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public void run() {
        if (actionItem.getWiredData().getParams().size() != 2) {
            return;
        }

        int movement = actionItem.getWiredData().getParams().get(PARAM_MOVEMENT);
        int rotation = actionItem.getWiredData().getParams().get(PARAM_ROTATION);

        for (Long itemId : selectedItems) {
            RoomItemFloor floorItem = actionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null /*|| floorItem instanceof DiceFloorItem*/) {
                actionItem.getWiredData().getSelectedIds().remove(itemId);
                continue;
            }

            Position currentPosition = floorItem.getPosition().copy();
            Position newPosition = this.handleMovement(currentPosition.copy(), movement);

            int newRotation = this.handleRotation(floorItem.getRotation(), rotation);
            boolean rotationChanged = newRotation != floorItem.getRotation();

            if (actionItem.getRoom().getItems().moveFloorItem(floorItem.getId(), newPosition, newRotation, false)) {
                if (!rotationChanged) {
                    actionItem.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
                } else {
                    actionItem.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
                }
            }

            floorItem.save();
        }
    }

    private Position handleMovement(Position point, int movementType) {
        switch (movementType) {
            case 0: {
                return point;
            }

            case 1: {
                int movement = this.random.nextInt(5);
                if (movement == 1) {
                    point = this.handleMovement(point, 4);
                    break;
                }
                if (movement == 2) {
                    point = this.handleMovement(point, 5);
                    break;
                }
                if (movement == 3) {
                    point = this.handleMovement(point, 6);
                    break;
                }
                point = this.handleMovement(point, 7);
                break;
            }

            case 2: {
                int i = this.random.nextInt(3);
                if (i == 1) {
                    point = this.handleMovement(point, 7);
                    break;
                }
                point = this.handleMovement(point, 5);
                break;
            }

            case 3: {
                int j = this.random.nextInt(3);
                if (j == 1) {
                    point = this.handleMovement(point, 4);
                    break;
                }
                point = this.handleMovement(point, 6);
                break;
            }

            case 4: {
                point.setY(point.getY() - 1);
                break;
            }

            case 5: {
                point.setX(point.getX() + 1);
                break;
            }

            case 6: {
                point.setY(point.getY() + 1);
                break;
            }

            case 7: {
                point.setX(point.getX() - 1);
            }
        }

        return point;
    }

    private int handleRotation(int rotation, int rotationType) {
        switch (rotationType) {
            case 0: {
                return rotation;
            }

            case 1: {
                if ((rotation += 2) <= 6) break;
                rotation = 0;
                break;
            }

            case 2: {
                if ((rotation -= 2) <= 6) break;
                rotation = 6;
                break;
            }

            case 3: {
                int i = this.random.nextInt(3);
                if (i == 1) {
                    rotation = this.handleRotation(rotation, 1);
                    break;
                }
                rotation = this.handleRotation(rotation, 2);
            }
        }

        return rotation;
    }
}
