package com.habboproject.server.game.rooms.objects.items.types.floor.wired;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.AdvancedFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.base.WiredActionItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.WiredItemData;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.actions.WiredActionItemData;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerPeriodically;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.item.FloorItemUpdateStateEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemExecuteEvent;
import com.habboproject.server.threads.executors.wired.events.WiredItemFlashEvent;
import com.habboproject.server.utilities.JsonFactory;
import com.habboproject.server.utilities.attributes.Stateable;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This system was inspired by Nillus' "habbod2".
 */
public abstract class WiredFloorItem extends AdvancedFloorItem<WiredItemExecuteEvent> implements WiredItemSnapshot.Refreshable, Stateable {
    /**
     * The data associated with this wired item
     */
    private WiredItemData wiredItemData = null;

    private boolean state;
    private boolean hasTicked = false;

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
    public WiredFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (!this.getExtraData().startsWith("{")) {
            this.setExtraData("{}");
        }

        this.load();
    }

    @Override
    public String getDataObject() {
        return JsonFactory.getInstance().toJson(wiredItemData);
    }

    /**
     * Turn the wired item data into a JSON object, and then save it to the database
     */
    public void save() {
        this.setExtraData(JsonFactory.getInstance().toJson(wiredItemData));
    }

    /**
     * Turn the JSON object into a usable WiredItemData object
     */
    public void load() {
        if (this.getExtraData().equals("{}")) {
            this.wiredItemData = (this instanceof WiredActionItem) ? new WiredActionItemData() : new WiredItemData();
            return;
        }

        this.wiredItemData = JsonFactory.getInstance().fromJson(this.getExtraData(), (this instanceof WiredActionItem) ? WiredActionItemData.class : WiredItemData.class);

        this.onDataRefresh();
    }

    @Override
    public void onPickup() {
        this.setExtraData("{}");
    }

    @Override
    public void onPlaced() {
        this.setExtraData("{}");
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (!(entity instanceof PlayerEntity)) {
            return true;
        }

        PlayerEntity p = (PlayerEntity) entity;

        if (!this.getRoom().getRights().hasRights(p.getPlayerId()) && !p.getPlayer().getPermissions().getRank().roomFullControl()) {
            return true;
        }

        this.flash();

        ((PlayerEntity) entity).getPlayer().getSession().send(this.getDialog());

        return true;
    }


    @Override
    public void refreshSnapshots() {
        List<Long> toRemove = Lists.newArrayList();
        this.getWiredData().getSnapshots().clear();

        for (long itemId : this.getWiredData().getSelectedIds()) {
            RoomItemFloor floorItem = this.getRoom().getItems().getFloorItem(itemId);

            if (floorItem == null) {
                toRemove.add(itemId);
                continue;
            }

            this.getWiredData().getSnapshots().put(itemId, new WiredItemSnapshot(floorItem));
        }

        for (long itemToRemove : toRemove) {
            this.getWiredData().getSelectedIds().remove(itemToRemove);
        }

        this.save();
    }

    public void flash() {
        this.switchState();

        final WiredItemFlashEvent flashEvent = new WiredItemFlashEvent();

        this.queueEvent(flashEvent);
    }

    public void switchState() {
        this.state = !this.state;
        this.sendUpdate();
    }

    @Override
    public void onTick() {
        super.onTick();

    }

    @Override
    protected void onEventComplete(WiredItemExecuteEvent event) {

    }

    /**
     * Get the wired item data object
     *
     * @return The wired item data object
     */
    public WiredItemData getWiredData() {
        return wiredItemData;
    }

    /**
     * Get the ID of the interface of the item which will tell the client which input fields to display
     *
     * @return The ID of the interface
     */
    public abstract int getInterface();

    /**
     * Get the packet to display the full dialog to the player
     *
     * @return The dialog message composer
     */
    public abstract MessageComposer getDialog();

    /**
     * Evaluate the wired trigger/action/condition
     *
     * @param entity The entity that's involved with this event
     * @param data   The data passed by the trigger
     * @return Whether or not the evaluation was a success
     */
    public abstract boolean evaluate(RoomEntity entity, Object data) throws ExecutionException, InterruptedException;

    /**
     * Will be executed when the data has been refreshed
     */
    public void onDataRefresh() {

    }

    /**
     * Will be executed when the data has been changed (different to the "onDataRefresh" event)
     */
    public void onDataChange() {

    }

    @Override
    public boolean getState() {
        return this.state;
    }
}
