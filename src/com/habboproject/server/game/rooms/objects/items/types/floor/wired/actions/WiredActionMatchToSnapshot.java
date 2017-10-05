package com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions;

import com.google.common.collect.Lists;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.DiceFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.actions.ActionMatchToSnapshotExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;

import java.util.Iterator;
import java.util.List;

public class WiredActionMatchToSnapshot extends WiredActionItem {
    private static final int PARAM_MATCH_STATE = 0;
    private static final int PARAM_MATCH_ROTATION = 1;
    private static final int PARAM_MATCH_POSITION = 2;

    public WiredActionMatchToSnapshot(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public int getInterface() {
        return 3;
    }

    @Override
    public void onEventComplete(WiredItemExecuteEvent event) {
        if (this.getWiredData().getSnapshots().size() == 0) {
            return;
        }

        final boolean matchState = this.getWiredData().getParams().get(PARAM_MATCH_STATE) == 1;
        final boolean matchRotation = this.getWiredData().getParams().get(PARAM_MATCH_ROTATION) == 1;
        final boolean matchPosition = this.getWiredData().getParams().get(PARAM_MATCH_POSITION) == 1;

        List<Long> toRemove = Lists.newArrayList();
        for (Iterator iterator = this.getWiredData().getSelectedIds().iterator(); iterator.hasNext();) {
            long itemId = (long) iterator.next();

            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
            } else {
                boolean rotationChanged = false;

                WiredItemSnapshot itemSnapshot = this.getWiredData().getSnapshots().get(itemId);
                if (itemSnapshot == null) continue;

                if (matchState && !(floorItem instanceof DiceFloorItem || floorItem instanceof HighscoreClassicFloorItem)) {
                    floorItem.setExtraData(itemSnapshot.getExtraData());
                    floorItem.saveData();
                }

                if (matchPosition || matchRotation) {
                    Position currentPosition = floorItem.getPosition().copy();

                    Position newPosition = new Position(itemSnapshot.getX(), itemSnapshot.getY(), itemSnapshot.getZ());

                    int currentRotation = floorItem.getRotation();

                    if (this.getRoom().getItems().moveFloorItem(floorItem.getId(), !matchPosition ? currentPosition : newPosition, matchRotation ? itemSnapshot.getRotation() : floorItem.getRotation(), true, false)) {
                        if (currentRotation != floorItem.getRotation()) {
                            rotationChanged = true;
                        }

                        if (!matchRotation || !rotationChanged && !matchState) {
                            this.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, this.getVirtualId(), floorItem.getVirtualId()));
                        }
                    }
                }

                if (matchRotation && rotationChanged)
                    this.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));

                floorItem.save();

                if (matchState) {
                    floorItem.sendUpdate();
                }
            }
        }

        if (toRemove.size() > 0) {
            for (long itemId : toRemove) {
                getWiredData().getSelectedIds().remove(itemId);
            }
        }
    }
}
