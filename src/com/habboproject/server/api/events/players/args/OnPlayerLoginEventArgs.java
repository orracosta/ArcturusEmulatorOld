package com.habboproject.server.api.events.players.args;

import com.habboproject.server.api.events.EventArgs;
import com.habboproject.server.api.game.players.BasePlayer;

public class OnPlayerLoginEventArgs extends EventArgs {
    private BasePlayer player;

    public OnPlayerLoginEventArgs(BasePlayer player) {
        this.player = player;
    }

    public BasePlayer getPlayer() {
        return this.player;
    }
}
