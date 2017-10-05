package com.habboproject.server.game.players.components;

import com.habboproject.server.api.game.players.data.components.PlayerPermissions;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.game.permissions.types.CommandPermission;
import com.habboproject.server.game.permissions.types.Rank;
import com.habboproject.server.game.players.types.Player;


public class PermissionComponent implements PlayerPermissions {
    private Player player;

    public PermissionComponent(Player player) {
        this.player = player;
    }

    @Override
    public Rank getRank() {
        return PermissionsManager.getInstance().getRank(this.player.getData().getRank());
    }

    @Override
    public boolean hasCommand(String key) {
        if(this.player.getData().getRank() == 255) {
            return true;
        }

        if (PermissionsManager.getInstance().getCommands().containsKey(key)) {
            CommandPermission permission = PermissionsManager.getInstance().getCommands().get(key);

            if (permission.getMinimumRank() <= this.getPlayer().getData().getRank()) {
                if ((permission.isVipOnly() && player.getData().isVip()) || !permission.isVipOnly())
                    return true;
            }
        } else if (key.equals("debug") && Comet.isDebugging) {
            return true;
        } else if (key.equals("dev")) {
            return true;
        }

        return false;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void dispose() {

    }
}
