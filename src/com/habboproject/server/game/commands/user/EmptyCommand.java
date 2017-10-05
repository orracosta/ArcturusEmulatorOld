package com.habboproject.server.game.commands.user;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.PetInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.bots.PlayerBotDao;
import com.habboproject.server.storage.queries.pets.PetDao;
import com.habboproject.server.storage.queries.player.inventory.InventoryDao;


public class EmptyCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            client.getPlayer().getInventory().getFloorItems().clear();
            client.getPlayer().getInventory().getWallItems().clear();

            InventoryDao.clearInventory(client.getPlayer().getId());

            sendNotif(Locale.getOrDefault("command.empty.emptied", "Your inventory was cleared."), client);
        } else {
            switch (params[0]) {
                default:

                    break;

                case "pets":
                    PetDao.deletePets(client.getPlayer().getId());
                    client.getPlayer().getPets().clearPets();

                    client.send(new PetInventoryMessageComposer(client.getPlayer().getPets().getPets()));

                    sendNotif(Locale.getOrDefault("command.empty.emptied_pets", "Your inventory was cleared."), client);
                    return;

                case "bots":
                    PlayerBotDao.deleteBots(client.getPlayer().getId());
                    client.getPlayer().getBots().clearBots();
                    client.send(new BotInventoryMessageComposer());

                    sendNotif(Locale.getOrDefault("command.empty.emptied_bots", "Your inventory was cleared."), client);
                    return;
            }
        }

        client.send(new UpdateInventoryMessageComposer());
    }

    @Override
    public String getPermission() {
        return "empty_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.empty.description");
    }
}