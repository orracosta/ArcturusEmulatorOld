package com.eu.habbo.plugin.events.furniture.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;
import gnu.trove.set.hash.THashSet;

/**
 * Created on 25-12-2015 17:07.
 */
public class WiredStackTriggeredEvent extends RoomUnitEvent
{
    /**
     * The item that triggered this stack.
     */
    public final InteractionWiredTrigger trigger;

    /**
     * The effects in this stack.
     */
    public final THashSet<InteractionWiredEffect> effects;

    /**
     * The conditions in this stack.
     */
    public final THashSet<InteractionWiredCondition> conditions;

    /**
     * This event triggers when a WiredStack is about to be executed.
     * @param roomUnit The RoomUnit that triggered this stack.
     * @param trigger The item that triggered this stack.
     * @param effects The effects in this stack.
     * @param conditions The conditions in this stack.
     */
    public WiredStackTriggeredEvent(RoomUnit roomUnit, InteractionWiredTrigger trigger, THashSet<InteractionWiredEffect> effects, THashSet<InteractionWiredCondition> conditions)
    {
        super(roomUnit);

        this.trigger = trigger;
        this.effects = effects;
        this.conditions = conditions;
    }
}
