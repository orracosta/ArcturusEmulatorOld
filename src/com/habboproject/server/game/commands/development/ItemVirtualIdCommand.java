package com.habboproject.server.game.commands.development;

import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;

public class ItemVirtualIdCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if(params.length == 0) {
            client.send(new AlertMessageComposer("There are currently " + ItemManager.getInstance().getItemIdToVirtualIds().size() + " item virtual IDs in memory."));
            return;
        }

        try {
            final int virtualId = Integer.parseInt(params[0]);

            client.send(new AlertMessageComposer("Virtual ID: " + virtualId + "\nReal ID: " + ItemManager.getInstance().getItemIdByVirtualId(virtualId)));
        } catch (Exception e) {

        }
    }

    @Override
    public String getPermission() {
        return "dev";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
