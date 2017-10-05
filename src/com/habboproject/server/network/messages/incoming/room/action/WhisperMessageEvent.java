package com.habboproject.server.network.messages.incoming.room.action;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.entries.RoomChatLogEntry;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.WhisperMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class WhisperMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        String text = msg.readString();

        String user = text.split(" ")[0];
        String message = text.replace(user + " ", "");

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        if(!client.getPlayer().getEntity().isVisible()) {
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        RoomEntity userTo = room.getEntities().getEntityByName(user, RoomEntityType.PLAYER);

        if (userTo == null || user.equals(client.getPlayer().getData().getUsername()))
            return;

        String filteredMessage = TalkMessageEvent.filterMessage(message);

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(message);

            if (filterResult.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                return;
            } else if (filterResult.wasModified()) {
                filteredMessage = filterResult.getMessage();
            }
        }

        if (!client.getPlayer().getEntity().onChat(filteredMessage))
            return;

        try {
            if (LogManager.ENABLED)
                LogManager.getInstance().getStore().getLogEntryContainer().put(new RoomChatLogEntry(room.getId(), client.getPlayer().getId(), Locale.getOrDefault("game.logging.whisper", "<Whisper to %username%>").replace("%username%", user) + " " + message));
        } catch (Exception ignored) {

        }

        client.send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage));

        if (!((PlayerEntity) userTo).getPlayer().ignores(client.getPlayer().getId()))
            ((PlayerEntity) userTo).getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), filteredMessage));

        for (PlayerEntity entity : client.getPlayer().getEntity().getRoom().getEntities().getWhisperSeers()) {
            if (entity.getPlayer().getId() != client.getPlayer().getId() && !user.equals(entity.getUsername()))
                entity.getPlayer().getSession().send(new WhisperMessageComposer(client.getPlayer().getEntity().getId(), "Whisper to " + user + ": " + filteredMessage));
        }
    }
}
