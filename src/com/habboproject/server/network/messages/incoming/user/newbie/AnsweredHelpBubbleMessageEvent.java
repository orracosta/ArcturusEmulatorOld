package com.habboproject.server.network.messages.incoming.user.newbie;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

/**
 * Created by brend on 04/03/2017.
 */
public class AnsweredHelpBubbleMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        /*if (client.getPlayer() == null || client.getPlayer().getEntity() == null)
            return;

        if (client.getPlayer().getData().getNewbieStep().equals("3")) {
            if (client.getPlayer().getEntity().getOnBubble() == 2) {
                ThreadManager.getInstance().executeSchedule(new NewbieBubbleEvent(null, client.getPlayer().getEntity(), "helpBubble/add/chat_input/nux.bot.info.chat.1", 0), 500, TimeUnit.MILLISECONDS);
            }

            if (client.getPlayer().getEntity().getOnBubble() == 1) {
                ThreadManager.getInstance().executeSchedule(new NewbieBubbleEvent(null, client.getPlayer().getEntity(), "helpBubble/add/BOTTOM_BAR_NAVIGATOR/nux.bot.info.navigator.1", 2), 500, TimeUnit.MILLISECONDS);
            }
        }*/
    }
}
