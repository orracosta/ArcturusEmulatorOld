package com.eu.habbo.plugin.events.furniture.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;
import gnu.trove.set.hash.THashSet;

public class WiredStackExecutedEvent extends RoomUnitEvent
{
    /**
     * The wired trigger for the stack.
     */
    public final InteractionWiredTrigger trigger;

    /**
     * The wired effects in this stack.
     */
    public final THashSet<InteractionWiredEffect> effects;

    /**
     * The conditions that were met.
     */
    public final THashSet<InteractionWiredCondition> conditions;

    /**
     * Triggered when a wired stack has succesfully been executed and all conditions are met.
     * @param room The Room this event applies to.
     * @param roomUnit The roomUnit that triggered this event.
     * @param trigger The wired trigger for the stack.
     * @param effects The wired effects in this stack.
     * @param conditions The conditions that were met.
     */
    public WiredStackExecutedEvent(Room room, RoomUnit roomUnit, InteractionWiredTrigger trigger, THashSet<InteractionWiredEffect> effects, THashSet<InteractionWiredCondition> conditions)
    {
        super(room, roomUnit);

        this.trigger = trigger;
        this.effects = effects;
        this.conditions = conditions;
    }
}
