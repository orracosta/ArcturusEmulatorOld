package com.habboproject.server.network.messages.incoming.user.details;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class UpdateChatStyleMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        boolean useOldChat = msg.readBoolean();

        if (client.getPlayer() == null) {
            return;
        }

        client.getPlayer().getSettings().setUseOldChat(useOldChat);
        PlayerDao.saveChatStyle(useOldChat, client.getPlayer().getId());
    }
}
