package com.habboproject.server.threads.executors.wired.actions;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.DiceFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionMoveToDirection;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionTeleportPlayer;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerCollision;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.utilities.RandomInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionMoveToDirectionExecuteEvent implements Callable<Boolean> {
    private static final int PARAM_START_DIR = 0;
    private static final int PARAM_ACTION_WHEN_BLOCKED = 1;

    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;

    public ActionMoveToDirectionExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public Boolean call() throws Exception {
        if (actionItem.getWiredData().getParams().size() != 2) {
            return false;
        }

        int startDir = actionItem.getWiredData().getParams().get(PARAM_START_DIR);
        int actionWhenBlocked = actionItem.getWiredData().getParams().get(PARAM_ACTION_WHEN_BLOCKED);

        for (Long itemId : selectedItems){
            RoomItemFloor floorItem = actionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null /*|| floorItem instanceof DiceFloorItem*/) {
                actionItem.getWiredData().getSelectedIds().remove(itemId);
                continue;
            }

            if (floorItem.getLastDirection() == 9 || ((WiredActionMoveToDirection)actionItem).needsChange()) {
                floorItem.setLastDirection(startDir);
                ((WiredActionMoveToDirection)actionItem).setNeedsChange(false);
            }

            Position currentPosition = floorItem.getPosition().copy();
            Position newPosition = this.handleMovementDirection(currentPosition, floorItem.getLastDirection(), floorItem.getRotation());

            if (!this.equals(newPosition, currentPosition)) {
                boolean continueRoute = false;

                List<WiredTriggerCollision> collisionWireds = actionItem.getRoom().getItems().getByClass(WiredTriggerCollision.class);

                if (collisionWireds.size() > 0) {
                    for (WiredTriggerCollision collisionWired : collisionWireds) {
                        if (collisionWired.getItemsOnStack().stream().filter(item -> item instanceof WiredActionTeleportPlayer).count() > 0) {
                            continueRoute = true;
                        }
                    }
                }

                PlayerEntity nearestEntity = null;

                if (actionItem.getRoom().getItems().verifyItemPosition(floorItem.getDefinition(), actionItem.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY()), currentPosition, true)) {
                    nearestEntity = floorItem.nearestPlayerEntity();
                    if (nearestEntity != null && this.isCollided(nearestEntity, newPosition)) {
                        floorItem.setCollision(nearestEntity);
                        WiredTriggerCollision.executeTriggers(nearestEntity);
                    }
                }

                if (actionItem.getRoom().getItems().moveFloorItem(floorItem.getId(), newPosition, floorItem.getRotation(), false, continueRoute)) {
                    actionItem.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
                } else {
                    int x = currentPosition.getX();
                    int y = currentPosition.getY();

                    int lastDirection = floorItem.getLastDirection();
                    int newDirection = 0;

                    switch (actionWhenBlocked) {
                        case 0:
                            newDirection = 9;
                            break;

                        case 1: {
                            if (lastDirection == 2) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                }
                            } else if (lastDirection == 6) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                }
                            } else if (lastDirection == 0) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                }
                            } else if (lastDirection == 4) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                }
                            } else if (lastDirection == 7) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                }
                            } else if (lastDirection == 1) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                }
                            } else if (lastDirection == 3) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                }
                            } else if (lastDirection == 5) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                }
                            }

                            break;
                        }

                        case 2: {
                            if (lastDirection == 2) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                }
                            } else if (lastDirection == 6) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                }
                            } else if (lastDirection == 0) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                }
                            } else if (lastDirection == 4) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                }
                            } else if (lastDirection == 7) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                }
                            } else if (lastDirection == 1) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                }
                            } else if (lastDirection == 3) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                }
                            } else if (lastDirection == 5) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                }
                            }

                            break;
                        }

                        case 3: {
                            if (lastDirection == 2) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                }
                            } else if (lastDirection == 6) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                }
                            } else if (lastDirection == 0) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                }
                            } else if (lastDirection == 4) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                }
                            } else if (lastDirection == 7) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                }
                            } else if (lastDirection == 1) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                }
                            } else if (lastDirection == 3) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                }
                            } else if (lastDirection == 5) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                }
                            }

                            break;
                        }

                        case 4: {
                            if (lastDirection == 2) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                }
                            } else if (lastDirection == 6) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                }
                            } else if (lastDirection == 0) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                }
                            } else if (lastDirection == 4) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                {
                                    newDirection = 2;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                {
                                    newDirection = 0;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                {
                                    newDirection = 6;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                {
                                    newDirection = 4;
                                    break;
                                }
                            } else if (lastDirection == 7) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                }
                            } else if (lastDirection == 1) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                }
                            } else if (lastDirection == 3) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                }
                            } else if (lastDirection == 5) {
                                if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                {
                                    newDirection = 3;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                {
                                    newDirection = 1;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                {
                                    newDirection = 7;
                                    break;
                                } else if (actionItem.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                {
                                    newDirection = 5;
                                    break;
                                }
                            }

                            break;
                        }

                        case 5: {
                            if (lastDirection == 2) {
                                newDirection = 6;
                            } else if (lastDirection == 6) {
                                newDirection = 2;
                            } else if (lastDirection == 0) {
                                newDirection = 4;
                            } else if (lastDirection == 4) {
                                newDirection = 0;
                            } else if (lastDirection == 1) {
                                newDirection = 5;
                            } else if (lastDirection == 5) {
                                newDirection = 1;
                            } else if (lastDirection == 7) {
                                newDirection = 3;
                            } else if (lastDirection == 3) {
                                newDirection = 7;
                            }

                            break;
                        }

                        case 6: {
                            newDirection = RandomInteger.getRandom(1, 7);
                            break;
                        }
                    }

                    floorItem.setLastDirection(newDirection);

                    newPosition = this.handleMovementDirection(currentPosition, newDirection, floorItem.getRotation());

                    if (actionItem.getRoom().getItems().verifyItemPosition(floorItem.getDefinition(), actionItem.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY()), currentPosition, true)) {
                        nearestEntity = floorItem.nearestPlayerEntity();
                        if (nearestEntity != null && this.isCollided(nearestEntity, newPosition)) {
                            floorItem.setCollision(nearestEntity);
                            WiredTriggerCollision.executeTriggers(nearestEntity);
                        }
                    }

                    if (actionItem.getRoom().getItems().moveFloorItem(floorItem.getId(), newPosition, floorItem.getRotation(), false)) {
                        actionItem.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
                    }
                }
            }
        }

        return true;
    }

    private boolean equals(Position posOne, Position posTwo) {
        return (posOne.getX() == posTwo.getX() && posOne.getY() == posTwo.getY());
    }

    public boolean isCollided(PlayerEntity entity, Position position) {
        return AffectedTile.tilesAdjecent(entity.getPosition(), position) && (entity.getPosition().getX() == position.getX() && entity.getPosition().getY() == position.getY());
    }

    private Position handleMovementDirection(Position coordinate, int direction, int rotation) {
        Position newPosition = coordinate.copy();

        switch (direction) {
            case 0:
            case 4:
            case 6:
            case 2:
            case 3:
            case 5:
            case 1:
            case 7: {
                newPosition = handleMovementDirection(newPosition, direction);
                break;
            }

            case 8: {
                switch (RandomInteger.getRandom(1, 5)) {
                    case 1: {
                        newPosition = handleMovementDirection(newPosition, 0);
                        break;
                    }
                    case 2: {
                        newPosition = handleMovementDirection(newPosition, 4);
                        break;
                    }

                    case 3: {
                        newPosition = handleMovementDirection(newPosition, 6);
                        break;
                    }
                    case 4: {
                        newPosition = handleMovementDirection(newPosition, 2);
                        break;
                    }
                }

                break;
            }
        }

        return newPosition;
    }

    private Position handleMovementDirection(Position position, int direction) {
        int x = position.getX();
        int y = position.getY();

        switch (direction) {
            case 4: {
                y++;
                break;
            }

            case 0: {
                y--;
                break;
            }

            case 6: {
                x--;
                break;
            }

            case 2: {
                x++;
                break;
            }

            case 3: {
                x++;
                y++;
                break;
            }

            case 5: {
                x--;
                y++;
                break;
            }

            case 1: {
                x++;
                y--;
                break;
            }

            case 7: {
                x--;
                y--;
                break;
            }
        }

        return new Position(x, y);
    }
}
