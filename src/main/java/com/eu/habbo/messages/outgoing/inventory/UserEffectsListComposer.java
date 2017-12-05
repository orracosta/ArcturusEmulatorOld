package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.inventory.EffectsComponent;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TObjectProcedure;

public class UserEffectsListComposer extends MessageComposer
{
    public final Habbo habbo;

    public UserEffectsListComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserEffectsListComposer);

        synchronized (this.habbo.getInventory().getEffectsComponent().effects)
        {
            this.response.appendInt(this.habbo.getInventory().getEffectsComponent().effects.size());
            this.habbo.getInventory().getEffectsComponent().effects.forEachValue(new TObjectProcedure<EffectsComponent.HabboEffect>()
            {
                @Override
                public boolean execute(EffectsComponent.HabboEffect effect)
                {
                    response.appendInt(effect.effect);
                    response.appendInt(0);
                    response.appendInt(effect.duration);
                    response.appendInt(effect.total);
                    response.appendInt(effect.activationTimestamp >= 0 ? Emulator.getIntUnixTimestamp() - effect.activationTimestamp : -1);
                    response.appendBoolean(effect.isActivated());
                    return true;
                }
            });
        }
        return this.response;
    }
}
