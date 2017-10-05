package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerCollision;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;
import com.habboproject.server.utilities.RandomInteger;

import java.util.Iterator;
import java.util.List;

public class WiredActionMoveToDirection extends WiredActionItem {
	private static final int PARAM_START_DIR = 0;
    private static final int PARAM_ACTION_WHEN_BLOCKED = 1;

    private boolean needsChange = false;

    public WiredActionMoveToDirection(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 13;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getWiredData().getParams().size() != 2) {
            return;
        }

        int startDir = this.getWiredData().getParams().get(PARAM_START_DIR);
        int actionWhenBlocked = this.getWiredData().getParams().get(PARAM_ACTION_WHEN_BLOCKED);

        List<Long> toRemove = Lists.newArrayList();
        for (Iterator iterator = this.getWiredData().getSelectedIds().iterator(); iterator.hasNext();) {
            long itemId = (long) iterator.next();

            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
            } else {
                if (floorItem.getLastDirection() == 9 || this.needsChange()) {
                    floorItem.setLastDirection(startDir);
                    this.setNeedsChange(false);
                }

                Position currentPosition = floorItem.getPosition().copy();
                Position newPosition = this.handleMovementDirection(currentPosition, floorItem.getLastDirection(), floorItem.getRotation());

                if (!this.equals(newPosition, currentPosition)) {
                    boolean continueRoute = false;

                    List<WiredTriggerCollision> collisionWireds = this.getRoom().getItems().getByClass(WiredTriggerCollision.class);

                    if (collisionWireds.size() > 0) {
                        for (WiredTriggerCollision collisionWired : collisionWireds) {
                            if (collisionWired.getItemsOnStack().stream().filter(item -> item instanceof WiredActionTeleportPlayer).count() > 0) {
                                continueRoute = true;
                            }
                        }
                    }

                    PlayerEntity nearestEntity = null;

                    if (this.getRoom().getItems().verifyItemPosition(floorItem.getDefinition(), this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY()), currentPosition, true)) {
                        nearestEntity = floorItem.nearestPlayerEntity();
                        if (nearestEntity != null && this.isCollided(nearestEntity, newPosition)) {
                            floorItem.setCollision(nearestEntity);
                            WiredTriggerCollision.executeTriggers(nearestEntity);
                        }
                    }

                    if (this.getRoom().getItems().moveFloorItem(floorItem.getId(), newPosition, floorItem.getRotation(), false, continueRoute)) {
                        this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
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
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    }
                                } else if (lastDirection == 6) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    }
                                } else if (lastDirection == 0) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    }
                                } else if (lastDirection == 4) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    }
                                } else if (lastDirection == 7) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    }
                                } else if (lastDirection == 1) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    }
                                } else if (lastDirection == 3) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    }
                                } else if (lastDirection == 5) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    }
                                }

                                break;
                            }

                            case 2: {
                                if (lastDirection == 2) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    }
                                } else if (lastDirection == 6) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    }
                                } else if (lastDirection == 0) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    }
                                } else if (lastDirection == 4) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    }
                                } else if (lastDirection == 7) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    }
                                } else if (lastDirection == 1) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    }
                                } else if (lastDirection == 3) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    }
                                } else if (lastDirection == 5) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    }
                                }

                                break;
                            }

                            case 3: {
                                if (lastDirection == 2) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    }
                                } else if (lastDirection == 6) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    }
                                } else if (lastDirection == 0) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    }
                                } else if (lastDirection == 4) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    }
                                } else if (lastDirection == 7) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    }
                                } else if (lastDirection == 1) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    }
                                } else if (lastDirection == 3) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    }
                                } else if (lastDirection == 5) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    }
                                }

                                break;
                            }

                            case 4: {
                                if (lastDirection == 2) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    }
                                } else if (lastDirection == 6) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    }
                                } else if (lastDirection == 0) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    }
                                } else if (lastDirection == 4) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y)) // derecha
                                    {
                                        newDirection = 2;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y - 1)) // arriba
                                    {
                                        newDirection = 0;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y)) // izq
                                    {
                                        newDirection = 6;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x, y + 1)) // abajo
                                    {
                                        newDirection = 4;
                                        break;
                                    }
                                } else if (lastDirection == 7) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    }
                                } else if (lastDirection == 1) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    }
                                } else if (lastDirection == 3) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
                                    {
                                        newDirection = 5;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    }
                                } else if (lastDirection == 5) {
                                    if (this.getRoom().getMapping().isValidItemPosition(x + 1, y + 1)) // abajo derecha
                                    {
                                        newDirection = 3;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x + 1, y - 1)) // arriba derecha
                                    {
                                        newDirection = 1;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y - 1)) // arriba izq
                                    {
                                        newDirection = 7;
                                        break;
                                    } else if (this.getRoom().getMapping().isValidItemPosition(x - 1, y + 1)) // abajo izq
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

                        if (this.getRoom().getItems().verifyItemPosition(floorItem.getDefinition(), this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY()), currentPosition, true)) {
                            nearestEntity = floorItem.nearestPlayerEntity();
                            if (nearestEntity != null && this.isCollided(nearestEntity, newPosition)) {
                                floorItem.setCollision(nearestEntity);
                                WiredTriggerCollision.executeTriggers(nearestEntity);
                            }
                        }

                        if (this.getRoom().getItems().moveFloorItem(floorItem.getId(), newPosition, floorItem.getRotation(), false)) {
                            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, 0, floorItem.getVirtualId()));
                        }
                    }
                }
            }
        }

        if (toRemove.size() > 0) {
            for (long itemId : toRemove) {
                getWiredData().getSelectedIds().remove(itemId);
            }
        }
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

    @Override
    public void onDataChange() {
        this.needsChange = true;
    }

    public boolean needsChange() {
        return needsChange;
    }

    public void setNeedsChange(boolean needsChange) {
        this.needsChange = needsChange;
    }
}
