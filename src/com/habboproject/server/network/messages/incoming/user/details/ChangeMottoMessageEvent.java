package com.habboproject.server.network.messages.incoming.user.details;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.filter.FilterResult;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.UpdateInfoMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import org.apache.commons.lang3.StringUtils;


public class ChangeMottoMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        String motto = msg.readString();

        if (!client.getPlayer().getPermissions().getRank().roomFilterBypass()) {
            FilterResult filterResult = RoomManager.getInstance().getFilter().filter(motto);

            if (filterResult.isBlocked()) {
                client.send(new AdvancedAlertMessageComposer(Locale.get("game.message.blocked").replace("%s", filterResult.getMessage())));
                return;
            } else if (filterResult.wasModified()) {
                motto = filterResult.getMessage();
            }
        }

        client.getPlayer().getData().setMotto(StringUtils.abbreviate(motto, 38));
        client.getPlayer().getData().save();

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new UpdateInfoMessageComposer(client.getPlayer().getEntity()));
        client.send(new UpdateInfoMessageComposer(-1, client.getPlayer().getEntity()));

        client.getPlayer().getAchievements().progressAchievement(AchievementType.MOTTO, 1);
        client.getPlayer().getQuests().progressQuest(QuestType.PROFILE_CHANGE_MOTTO);
    }
}
