package com.habboproject.server.threads.executors.wired.actions;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.freeze.FreezeTimerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.DiceFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredItemSnapshot;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreMostWinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreTeamFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.network.messages.outgoing.room.items.SlideObjectBundleMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.threads.CometThread;

import java.util.List;

/**
 * Created by brend on 11/03/2017.
 */
public class ActionMatchToSnapshotExecuteEvent implements CometThread {
    private static final int PARAM_MATCH_STATE = 0;
    private static final int PARAM_MATCH_ROTATION = 1;
    private static final int PARAM_MATCH_POSITION = 2;

    private final WiredActionItem actionItem;
    private final List<Long> selectedItems;

    public ActionMatchToSnapshotExecuteEvent(WiredActionItem actionItem, List<Long> selectedItems) {
        this.actionItem = actionItem;
        this.selectedItems = selectedItems;
    }

    @Override
    public void run() {
        if (actionItem.getWiredData().getSnapshots().size() == 0) {
            return;
        }

        boolean matchState = actionItem.getWiredData().getParams().get(PARAM_MATCH_STATE) == 1;
        boolean matchRotation = actionItem.getWiredData().getParams().get(PARAM_MATCH_ROTATION) == 1;
        boolean matchPosition = actionItem.getWiredData().getParams().get(PARAM_MATCH_POSITION) == 1;

        for (long itemId : selectedItems) {
            boolean rotationChanged = false;

            RoomItemFloor floorItem = actionItem.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                actionItem.getWiredData().getSelectedIds().remove(itemId);
                continue;
            }

            WiredItemSnapshot itemSnapshot = actionItem.getWiredData().getSnapshots().get(itemId);

            if (itemSnapshot == null)
                continue;

            if (!(!matchState /*|| floorItem instanceof DiceFloorItem*/ || floorItem instanceof HighscoreClassicFloorItem || floorItem instanceof HighscoreTeamFloorItem || floorItem instanceof HighscoreMostWinFloorItem)) {
                if (floorItem instanceof BanzaiTimerFloorItem && ((BanzaiTimerFloorItem)floorItem).isRunning()) {
                    floorItem.onInteract(null, -1, true);
                }

                if (floorItem instanceof FootballTimerFloorItem && ((FootballTimerFloorItem)floorItem).isRunning()) {
                    floorItem.onInteract(null, -1, true);
                }

                if (floorItem instanceof FreezeTimerFloorItem && ((FreezeTimerFloorItem)floorItem).isRunning()) {
                    floorItem.onInteract(null, -1, true);
                }

                floorItem.setExtraData(itemSnapshot.getExtraData());

                if (floorItem instanceof BanzaiTimerFloorItem && ((BanzaiTimerFloorItem)floorItem).isInterrupted()) {
                    floorItem.onInteract(null, Integer.parseInt(floorItem.getExtraData()), true);
                }

                if (floorItem instanceof FootballTimerFloorItem && ((FootballTimerFloorItem)floorItem).isInterrupted()) {
                    floorItem.onInteract(null, Integer.parseInt(floorItem.getExtraData()), true);
                }

                if (floorItem instanceof FreezeTimerFloorItem && ((FreezeTimerFloorItem)floorItem).isInterrupted()) {
                    floorItem.onInteract(null, Integer.parseInt(floorItem.getExtraData()), true);
                }
            }

            if (matchPosition || matchRotation) {
                Position currentPosition = floorItem.getPosition().copy();
                Position newPosition = new Position(itemSnapshot.getX(), itemSnapshot.getY());

                int currentRotation = floorItem.getRotation();
                if (actionItem.getRoom().getItems().moveFloorItem(floorItem.getId(), !matchPosition ? currentPosition : newPosition, matchRotation ? itemSnapshot.getRotation() : floorItem.getRotation(), true)) {
                    if (currentRotation != floorItem.getRotation()) {
                        rotationChanged = true;
                    }

                    newPosition.setZ(floorItem.getPosition().getZ());

                    if (!matchRotation || !rotationChanged && !matchState) {
                        actionItem.getRoom().getEntities().broadcastMessage(new SlideObjectBundleMessageComposer(currentPosition, newPosition, 0, actionItem.getVirtualId(), floorItem.getVirtualId()));
                    }
                }
            }

            if (matchRotation && rotationChanged) {
                actionItem.getRoom().getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(floorItem));
            }

            floorItem.save();

            if (matchState) {
                floorItem.sendUpdate();
            }
        }
    }
}
