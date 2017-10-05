package com.habboproject.server.game.commands.vip;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.google.common.collect.Lists;

import java.util.List;


public class RedeemCreditsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        int coinsToGive = 0;
        int diamondsToGive = 0;

        List<Long> itemsToRemove = Lists.newArrayList();

        if (!client.getPlayer().getInventory().itemsLoaded()) {
            sendNotif(Locale.getOrDefault("command.redeemcredits.inventory", "Please open your inventory before executing this command!"), client);
            return;
        }

        for (PlayerItem playerItem : client.getPlayer().getInventory().getFloorItems().values()) {
            if (playerItem == null || playerItem.getDefinition() == null) continue;

            String itemName = playerItem.getDefinition().getItemName();

            if (itemName.startsWith("CF_") || itemName.startsWith("CFC_")) {
                try {
                    if (itemName.contains("_diamond_")) {
                        diamondsToGive += Integer.parseInt(itemName.split("_diamond_")[1]);
                    } else {
                        coinsToGive += Integer.parseInt(itemName.split("_")[1]);
                    }

                    itemsToRemove.add(playerItem.getId());

                    RoomItemDao.deleteItem(playerItem.getId());
                } catch (Exception ignored) {

                }
            }
        }

        if (itemsToRemove.size() == 0) {
            return;
        }

        for (long itemId : itemsToRemove) {
            client.getPlayer().getInventory().removeFloorItem(itemId);
        }

        itemsToRemove.clear();

        client.send(new UpdateInventoryMessageComposer());

        if (diamondsToGive > 0) {
            client.getPlayer().getData().increasePoints(diamondsToGive);
        }

        if (coinsToGive > 0) {
            client.getPlayer().getData().increaseCredits(coinsToGive);
        }

        if (diamondsToGive > 0 || coinsToGive > 0) {
            client.getPlayer().sendBalance();
            client.getPlayer().getData().save();
        }
    }


    @Override
    public String getPermission() {
        return "redeemcredits_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.redeemcredits.description");
    }
}
