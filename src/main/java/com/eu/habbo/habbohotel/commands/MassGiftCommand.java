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

import java.util.Map;

/**
 * Created on 22-11-2015 19:32.
 */
public class MassGiftCommand extends Command
{
    public MassGiftCommand()
    {
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_massgift").split(";");
        super.permission = "cmd_massgift";
    }

    @Override
    public boolean handle(final GameClient gameClient, String[] params) throws Exception
    {
        if(params.length >= 2)
        {
            try
            {
                int itemId = Integer.valueOf(params[1]);

                if(itemId < 0)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_gift.not_a_number"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                final Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(itemId);

                if(baseItem == null)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_gift.not_found").replace("%itemid%", itemId + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                String message = "";

                if(params.length > 2)
                {
                    for (int i = 2; i < params.length; i++)
                    {
                        message += params[i] + " ";
                    }
                }

                final String finalMessage = message;
                Emulator.getThreading().run(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
                        {
                            Habbo habbo = set.getValue();

                            HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "");

                            Item giftItem = Emulator.getGameEnvironment().getItemManager().getItem((Integer) Emulator.getGameEnvironment().getCatalogManager().giftFurnis.values().toArray()[Emulator.getRandom().nextInt(Emulator.getGameEnvironment().getCatalogManager().giftFurnis.size())]);

                            String extraData = "1\t" + item.getId();
                            extraData += "\t0\t0\t0\t"+ finalMessage +"\t0\t0";

                            Emulator.getGameEnvironment().getItemManager().createGift(habbo.getHabboInfo().getUsername(), giftItem, extraData, 0, 0);

                            habbo.getClient().sendResponse(new InventoryRefreshComposer());
                        }
                    }
                });


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
