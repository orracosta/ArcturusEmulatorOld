package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.users.Habbo;

public class UserWiredRewardReceived extends UserEvent
{
    /**
     * The wired effect that gave the reward.
     */
    public final WiredEffectGiveReward wiredEffectGiveReward;

    /**
     * Type of reward being given.
     */
    public final String type;

    /**
     * Reward being given.
     */
    public String value;

    /**
     * Triggered whenever a Habbo succesfully receives a reward.
     * Change value to set the new reward.
     * Cancel the event to prevent the reward from being given.
     * @param habbo The Habbo this event applies to.
     * @param wiredEffectGiveReward The wired effect that gave the reward.
     * @param type  Type of reward being given.
     * @param value Reward being given.
     */
    public UserWiredRewardReceived(Habbo habbo, WiredEffectGiveReward wiredEffectGiveReward, String type, String value)
    {
        super(habbo);

        this.wiredEffectGiveReward = wiredEffectGiveReward;
        this.type = type;
        this.value = value;
    }
}
