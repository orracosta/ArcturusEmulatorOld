package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 21-11-2015 22:05.
 */
public class GiftCommand extends Command
{
    public GiftCommand()
    {
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_gift").split(";");
        super.permission = "cmd_gift";
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length >= 3)
        {
            try
            {
                String username = params[1];
                int itemId = Integer.valueOf(params[2]);

                if(itemId < 0)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_gift.not_a_number"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(itemId);

                if(baseItem == null)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_gift.not_found").replace("%itemid%", itemId + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

                if(habbo == null)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_gift.user_not_found").replace("%username%", username), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                String message = "";

                if(params.length > 3)
                {
                    for (int i = 3; i < params.length; i++)
                    {
                        message += params[i] + " ";
                    }
                }

                final String finalMessage = message;

                HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "");

                Item giftItem = Emulator.getGameEnvironment().getItemManager().getItem((Integer)Emulator.getGameEnvironment().getCatalogManager().giftFurnis.values().toArray()[Emulator.getRandom().nextInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size())]);

                String extraData = "1\t" + item.getId();
                extraData += "\t0\t0\t0\t"+ finalMessage +"\t0\t0";

                Emulator.getGameEnvironment().getItemManager().createGift(habbo.getHabboInfo().getUsername(), giftItem, extraData, 0, 0);

                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_gift").replace("%username%", username).replace("%itemname%", item.getBaseItem().getName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                habbo.getClient().sendResponse(new InventoryRefreshComposer());

                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_gift.not_a_number"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        }

        return false;
    }
}
