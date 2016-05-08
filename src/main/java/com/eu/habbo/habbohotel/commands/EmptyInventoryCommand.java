package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryItemsComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryUpdateItemComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.threading.runnables.QueryDeleteHabboItems;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Created on 6-8-2015 20:24.
 */
public class EmptyInventoryCommand extends Command
{
    public EmptyInventoryCommand()
    {
        super.permission = "cmd_empty";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_empty").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 1 || (params.length >= 2 && !params[1].equals(Emulator.getTexts().getValue("generic.yes"))))
        {
           if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
           {
               if(gameClient.getHabbo().getHabboInfo().getCurrentRoom().getUserCount() > 10)
               {
                   gameClient.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.succes.cmd_empty.verify").replace("%generic.yes%", Emulator.getTexts().getValue("generic.yes"))));
               }
               else
               {
                   gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_empty.verify").replace("%generic.yes%", Emulator.getTexts().getValue("generic.yes")), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
               }
           }

            return true;
        }

        if(params.length >= 2 && params[1].equalsIgnoreCase(Emulator.getTexts().getValue("generic.yes")))
        {
            Habbo habbo = gameClient.getHabbo();
            if(params.length == 3 && gameClient.getHabbo().hasPermission("acc_empty_others"))
            {
                habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[2]);
            }

            TIntObjectMap<HabboItem> items = new TIntObjectHashMap<HabboItem>();
            items.putAll(habbo.getHabboInventory().getItemsComponent().getItems());
            habbo.getHabboInventory().getItemsComponent().getItems().clear();

            Emulator.getThreading().run(new QueryDeleteHabboItems(items));

            habbo.getClient().sendResponse(new InventoryRefreshComposer());
            habbo.getClient().sendResponse(new InventoryItemsComposer(gameClient.getHabbo()));

            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_empty.cleared"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }

        return true;
    }
}
