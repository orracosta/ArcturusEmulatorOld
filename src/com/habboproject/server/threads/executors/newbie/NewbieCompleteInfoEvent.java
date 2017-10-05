package com.habboproject.server.threads.executors.newbie;

import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 05/03/2017.
 */
public class NewbieCompleteInfoEvent implements CometThread {
    private final Player player;

    public NewbieCompleteInfoEvent(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        this.player.getData().setNewbieStep("4");
    }
}
