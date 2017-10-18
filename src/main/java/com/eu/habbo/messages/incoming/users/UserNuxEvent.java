package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.NewUserGift;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.unknown.NewUserGiftComposer;
import com.eu.habbo.messages.outgoing.unknown.NuxAlertComposer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserNuxEvent extends MessageHandler
{
    public static Map<Integer, String> keys = new HashMap<Integer, String>()
    {
        {
            put(1, "BOTTOM_BAR_RECEPTION");
            put(2, "BOTTOM_BAR_NAVIGATOR");
            put(3, "CHAT_INPUT");
            put(4, "CHAT_HISTORY_BUTTON");
            put(5, "MEMENU_CLOTHES");
            put(6, "BOTTOM_BAR_CATALOGUE");
            put(7, "CREDITS_BUTTON");
            put(8, "DUCKETS_BUTTON");
            put(9, "DIAMONDS_BUTTON");
            put(10, "FRIENDS_BAR_ALL_FRIENDS");
            put(11, "BOTTOM_BAR_NAVIGATOR");
        }
    };
    @Override
    public void handle() throws Exception
    {
        handle(this.client.getHabbo());
    }

    public static void handle(Habbo habbo)
    {
        habbo.getHabboStats().nux = true;
        int step = habbo.getHabboStats().nuxStep++;

        if (keys.containsKey(step))
        {
            habbo.getClient().sendResponse(new NuxAlertComposer("helpBubble/add/" + keys.get(step) + "/" + Emulator.getTexts().getValue("nux.step." + step)));
        }
        else if (!habbo.getHabboStats().nuxReward)
        {
//            List<List<NewUserGift>> gifts = new ArrayList<>();
//            gifts.add(Emulator.getGameEnvironment().getItemManager().getNewUserGifts());
//            habbo.getClient().sendResponse(new NewUserGiftComposer(gifts));
        }
    }
}
