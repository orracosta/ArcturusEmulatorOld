package com.eu.habbo.core;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserRemoveComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.users.UserSavedMottoEvent;

/**
 * Created on 24-10-2015 20:40.
 */
public class Easter
{
    @EventHandler
    public static void onUserChangeMotto(UserSavedMottoEvent event)
    {
        if(event.newMotto.equalsIgnoreCase("crickey!"))
        {
            event.habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(event.newMotto, event.habbo, event.habbo, RoomChatMessageBubbles.ALERT)));

            Room room = event.habbo.getHabboInfo().getCurrentRoom();

            room.sendComposer(new RoomUserRemoveComposer(event.habbo.getRoomUnit()).compose());
            room.sendComposer(new EasterCrickeyComposer(event.habbo).compose());

        }
    }
}
