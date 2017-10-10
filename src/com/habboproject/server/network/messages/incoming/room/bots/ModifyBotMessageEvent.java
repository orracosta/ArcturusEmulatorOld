package com.habboproject.server.network.messages.incoming.room.bots;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.utilities.RandomInteger;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ModifyBotMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        PlayerEntity entity = client.getPlayer().getEntity();

        if (entity == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();

        if (room == null || (!room.getRights().hasRights(client.getPlayer().getId()) && !client.getPlayer().getPermissions().getRank().roomFullControl()) || !(client.getPlayer().getPermissions().getRank().roomFullAcessPublic() && room.getData().getOwnerId() == 0)) {
            return;
        }

        int botId = msg.readInt();
        int action = msg.readInt();
        String data = msg.readString();

        BotEntity botEntity = room.getEntities().getEntityByBotId(botId);

        switch (action) {
            case 1:
                String figure = entity.getFigure();
                String gender = entity.getGender();

                botEntity.getData().setFigure(figure);
                botEntity.getData().setGender(gender);

                room.getEntities().broadcastMessage(new UpdateInfoMessageComposer(botEntity));
                break;

            case 2:
                String[] data1 = data.split(";");

                List<String> messages = Arrays.asList(data1[0].split("\r"));

                String automaticChat = data1[2];
                String speakingInterval = data1[4];

                if (speakingInterval.isEmpty() || !StringUtils.isNumeric(speakingInterval) || Integer.parseInt(speakingInterval) < 7) {
                    speakingInterval = "7";
                }

                for (String message : messages) {
                    FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

                    if (filterResult.isBlocked()) {
                        client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                        return;
                    }
//                    else if (filterResult.wasModified()) {
//                        messages.remove(message);
//                        messages.add(filterResult.getMessage());
//                    }
                }

                botEntity.getData().setMessages((String[]) messages.toArray());
                botEntity.getData().setChatDelay(Integer.parseInt(speakingInterval));
                botEntity.getData().setAutomaticChat(Boolean.parseBoolean(automaticChat));
                break;

            case 3:
                // Relax
                switch (botEntity.getData().getMode()) {
                    case "default":
                        botEntity.getData().setMode("relaxed");
                        break;

                    case "relaxed":
                        botEntity.getData().setMode("default");
                        break;
                }

                botEntity.getData().save();
                break;

            case 4:
                if (botEntity.getDanceId() != 0) {
                    botEntity.setDanceId(0);
                } else {
                    // Dance
                    int danceId = RandomInteger.getRandom(1, 4);

                    botEntity.setDanceId(danceId);
                    room.getEntities().broadcastMessage(new DanceMessageComposer(botEntity.getId(), danceId));
                }
                break;

            case 5:
                // Change name
                final String botName = room.getBots().getAvailableName(data);
                room.getBots().changeBotName(botEntity.getUsername(), botName);

                botEntity.getData().setUsername(botName);

                room.getEntities().broadcastMessage(new AvatarsMessageComposer(botEntity));
                break;
        }

        botEntity.getData().save();
    }
}
