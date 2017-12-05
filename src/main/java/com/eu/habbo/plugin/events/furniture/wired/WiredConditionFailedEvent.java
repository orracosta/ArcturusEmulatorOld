package com.eu.habbo.plugin.events.furniture.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;

public class WiredConditionFailedEvent extends RoomUnitEvent
{
    /**
     * The wired trigger item.
     */
    public final InteractionWiredTrigger trigger;

    /**
     * The condition that failed to be met.
     */
    public final InteractionWiredCondition condition;

    /**
     * Cancelling this event equals the condition being met and therefor allows further execution.
     * @param room The Room this event applies to.
     * @param roomUnit The RoomUnit this event applies to.
     * @param trigger The wired trigger item.
     * @param condition The condition that failed to be met.
     */
    public WiredConditionFailedEvent(Room room, RoomUnit roomUnit, InteractionWiredTrigger trigger, InteractionWiredCondition condition)
    {
        super(room, roomUnit);
        this.trigger = trigger;
        this.condition = condition;
    }
}
