package com.habboproject.server.network.messages.incoming.room.bots;

import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.room.bots.BotConfigMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

public class BotConfigMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int botId = msg.readInt();
        int skillId = msg.readInt();

        BotEntity entity = client.getPlayer().getEntity().getRoom().getEntities().getEntityByBotId(botId);

        if (entity == null) {
            return;
        }

        String message = "";

        switch (skillId) {
            case 2:
                for (int i = 0; i < entity.getData().getMessages().length; i++) {
                    message += entity.getData().getMessages()[i] + "\r";
                }

                message += ";#;";
                message += String.valueOf(entity.getData().isAutomaticChat());
                message += ";#;";
                message += String.valueOf(entity.getData().getChatDelay());
                break;

            case 5:
                message = entity.getUsername();
                break;
        }

        client.send(new BotConfigMessageComposer(entity.getBotId(), skillId, message));
    }
}
