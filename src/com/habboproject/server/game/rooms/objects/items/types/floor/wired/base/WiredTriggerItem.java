package com.habboproject.server.game.rooms.objects.items.types.floor.wired.base;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.actions.WiredActionKickUser;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonRandomEffect;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.addons.WiredAddonUnseenEffect;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerEnterRoom;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.wired.dialog.WiredTriggerMessageComposer;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.wired.WiredActionExecuteEvent;
import com.habboproject.server.utilities.RandomInteger;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;


public abstract class WiredTriggerItem extends WiredFloorItem {
    private static Logger log = Logger.getLogger(WiredTriggerItem.class.getName());

    /**
     * The default constructor
     *
     * @param id       The ID of the item
     * @param itemId   The ID of the item definition
     * @param room     The instance of the room
     * @param owner    The ID of the owner
     * @param x        The position of the item on the X axis
     * @param y        The position of the item on the Y axis
     * @param z        The position of the item on the z axis
     * @param rotation The orientation of the item
     * @param data     The JSON object associated with this item
     */
    public WiredTriggerItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean evaluate(RoomEntity entity, Object data) {
        try {
            // create empty list for all wired actions on the current tile
            List<WiredActionItem> wiredActions = Lists.newArrayList();

            // create empty list for all wired conditions on current tile
            List<WiredConditionItem> wiredConditions = Lists.newArrayList();

            // used by addons
            boolean useRandomEffect = false;
            WiredAddonUnseenEffect unseenEffectItem = null;

            boolean canExecute = true;

            // Wired animation
            this.flash();

            // loop through all items on this tile
            for (RoomItemFloor floorItem : this.getItemsOnStack()) {
                if (floorItem instanceof WiredActionItem) {

                    // if the item is a wired action, add it to the list of actions
                    wiredActions.add(((WiredActionItem) floorItem));
                } else if (floorItem instanceof WiredConditionItem) {

                    // if the item is a wired condition, add it to the list of conditions
                    wiredConditions.add((WiredConditionItem) floorItem);
                } else if (floorItem instanceof WiredAddonUnseenEffect && unseenEffectItem == null) {

                    unseenEffectItem = ((WiredAddonUnseenEffect) floorItem);
                } else if (floorItem instanceof WiredAddonRandomEffect) {
                    useRandomEffect = true;
                }
            }

            if (unseenEffectItem != null && unseenEffectItem.getSeenEffects().size() >= wiredActions.size()) {
                unseenEffectItem.getSeenEffects().clear();
            }

            // loop through the conditions and check whether or not we can perform the action
            for (WiredConditionItem conditionItem : wiredConditions) {
                conditionItem.flash();

                if (!conditionItem.evaluate(entity, data)) {
                    canExecute = false;
                }
            }

            // tell the trigger that the item can execute, but hasn't executed just yet!
            // (just incase you wanna cancel the event that triggered this or do something else... who knows?!?!)
            this.preActionTrigger(entity, data);

            // if we can perform the action, let's perform it!
            if (canExecute && wiredActions.size() >= 1) {
                // if the execution was a success, this will be set to true and returned so that the
                // event that called this wired trigger can do what it needs to do
                boolean wasSuccess = false;

                if (useRandomEffect) {
                    int itemIndex = RandomInteger.getRandom(0, wiredActions.size() - 1);

                    WiredActionItem actionItem = wiredActions.get(itemIndex);

                    if (actionItem != null) {
                        if (this.executeEffect(actionItem, entity, data)) {
                            wasSuccess = true;
                        }
                    }

                    return wasSuccess;
                } else if (unseenEffectItem != null) {
                    for (WiredActionItem actionItem : wiredActions) {
                        if (!unseenEffectItem.getSeenEffects().contains(actionItem.getId())) {
                            unseenEffectItem.getSeenEffects().add(actionItem.getId());

                            if (this.executeEffect(actionItem, entity, data))
                                wasSuccess = true;
                            break;
                        }
                    }

                    return wasSuccess;
                } else {
                    for (WiredActionItem actionItem : wiredActions) {
                        if (this.executeEffect(actionItem, entity, data)) {
                            wasSuccess = true;
                        }
                    }
                }

                return wasSuccess;
            }
        } catch (Exception e) {
            //log wired
            //log.error("Error during WiredTrigger evaluation", e);
        }

        // tell the event that called the trigger that it was not a success!
        return false;
    }

    private boolean executeEffect(WiredActionItem actionItem, RoomEntity entity, Object data) {
        if (this instanceof WiredTriggerEnterRoom && actionItem instanceof WiredActionKickUser) {
            if (entity != null) {
                if (entity instanceof PlayerEntity && ((PlayerEntity) entity).getPlayer() != null) {
                    if (!((PlayerEntity) entity).getPlayer().getPermissions().getRank().roomKickable()) {
                        return false;
                    }
                }
            }
        }

        return actionItem.evaluate(entity, data);
    }

    @Override
    public MessageComposer getDialog() {
        return new WiredTriggerMessageComposer(this);
    }

    public List<WiredActionItem> getIncompatibleActions() {
        // create an empty list to add the incompatible actions
        List<WiredActionItem> incompatibleActions = Lists.newArrayList();

        // check whether or not this current trigger supplies a player
        if (!this.suppliesPlayer()) {
            // if it doesn't, loop through all items on current tile
            for (RoomItemFloor floorItem : this.getItemsOnStack()) {
                if (floorItem instanceof WiredActionItem) {
                    // check whether the item needs a player to perform its action
                    if (((WiredActionItem) floorItem).requiresPlayer()) {
                        // if it does, add it to the incompatible actions list!
                        incompatibleActions.add(((WiredActionItem) floorItem));
                    }
                }
            }
        }

        return incompatibleActions;
    }

    public void preActionTrigger(RoomEntity entity, Object data) {
        // override me if u want to!!!!111one
    }

    public abstract boolean suppliesPlayer();


}
