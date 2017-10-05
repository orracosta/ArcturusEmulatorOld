package com.habboproject.server.game.rooms.objects.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.game.items.types.LowPriorityItemProcessor;
import com.habboproject.server.game.rooms.objects.BigRoomFloorObject;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.types.floor.rollable.RollableFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.utilities.attributes.Attributable;

import java.util.HashMap;
import java.util.Map;

public abstract class RoomItem extends BigRoomFloorObject implements Attributable {
    protected int itemId;
    protected int ownerId;
    protected String ownerName;

    protected int groupId;

    protected int rotation;

    protected int ticksTimer;

    protected boolean needsUpdate;

    private LimitedEditionItemData limitedEditionItemData;
    private Map<String, Object> attributes;

    public RoomItem(long id, Position position, Room room) {
        super(id, position, room);
        this.ticksTimer = -1;
    }

    public void setLimitedEditionItemData(LimitedEditionItemData limitedEditionItemData) {
        this.limitedEditionItemData = limitedEditionItemData;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getOwner() {
        return this.ownerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getRotation() {
        return this.rotation;
    }

    public boolean needsUpdate() {
        return needsUpdate;
    }

    public void needsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    public final boolean requiresTick() {
        return this.hasTicks() || this instanceof WiredFloorItem;
    }

    protected final boolean hasTicks() {
        return (this.ticksTimer > 0);
    }

    protected final void setTicks(int time) {
        this.ticksTimer = time;

        if (this instanceof RollableFloorItem) {
            LowPriorityItemProcessor.getInstance().submit(((RoomItemFloor) this));
        }
    }

    protected final void cancelTicks() {
        this.ticksTimer = -1;
    }

    public final void tick() {
        this.onTick();

        if (this.ticksTimer > 0) {
            this.ticksTimer--;
        }

        if (this.ticksTimer == 0) {
            this.cancelTicks();
            this.onTickComplete();
        }
    }

    protected void onTick() {
        // Override this
    }

    protected void onTickComplete() {
        // Override this
    }

    public void onPlaced() {
        // Override this
    }

    public void onPickup() {
        // Override this
    }

    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        // Override this
        return true;
    }

    public void onLoad() {
        // Override this
    }

    public void onUnload() {
        // Override this
    }

    public void onEntityLeaveRoom(RoomEntity entity) {
        // Override this
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        if(this.attributes == null) {
            this.attributes = new HashMap<>();
        }

        if (this.attributes.containsKey(attributeKey)) {
            this.attributes.replace(attributeKey, attributeValue);
        } else {
            this.attributes.put(attributeKey, attributeValue);
        }
    }

    @Override
    public Object getAttribute(String attributeKey) {
        if(this.attributes == null) {
            this.attributes = new HashMap<>();
        }

        return this.attributes.get(attributeKey);
    }

    @Override
    public boolean hasAttribute(String attributeKey) {
        if(this.attributes == null) {
            this.attributes = new HashMap<>();
        }

        return this.attributes.containsKey(attributeKey);
    }

    @Override
    public void removeAttribute(String attributeKey) {
        if(this.attributes == null) {
            this.attributes = new HashMap<>();
        }

        this.attributes.remove(attributeKey);
    }

    public abstract void serialize(IComposer msg);

    public abstract ItemDefinition getDefinition();

    public abstract boolean toggleInteract(boolean state);

    public abstract void sendUpdate();

    public abstract void save();

    public abstract void saveData();

    public abstract String getExtraData();

    public abstract void setExtraData(String data);

    public void dispose() {

    }

    public LimitedEditionItemData getLimitedEditionItemData() {
        return limitedEditionItemData;
    }
}
