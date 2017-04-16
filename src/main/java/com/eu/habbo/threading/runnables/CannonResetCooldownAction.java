package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionCannon;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class CannonResetCooldownAction implements Runnable
{
    private final InteractionCannon cannon;

    public CannonResetCooldownAction(InteractionCannon cannon)
    {
        this.cannon = cannon;
    }

    @Override
    public void run()
    {
        if(this.cannon != null) {
            this.cannon.cooldown = false;
        }
    }
}
