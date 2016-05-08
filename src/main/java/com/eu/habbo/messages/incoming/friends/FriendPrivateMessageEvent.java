package com.eu.habbo.messages.incoming.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendChatMessageComposer;
import com.eu.habbo.plugin.events.users.friends.UserFriendChatEvent;

/**
 * Created on 26-8-2014 10:45.
 */
public class FriendPrivateMessageEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        int userId = this.packet.readInt();
        String message = this.packet.readString();

        MessengerBuddy buddy = this.client.getHabbo().getMessenger().getFriend(userId);

        //TODO Incorrect behaviour (Really low chance)
        if(buddy == null) {
            if(message.startsWith(":"))
            {
                CommandHandler.handleCommand(this.client, message);
                return;
            }
            Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new FriendChatMessageComposer(new Message(0, 0, this.client.getHabbo().getHabboInfo().getUsername() + ": " + message)).compose(), "acc_staff_chat", this.client);
            return;
        }

        UserFriendChatEvent event = new UserFriendChatEvent(this.client.getHabbo(), buddy, message);
        if(Emulator.getPluginManager().fireEvent(event).isCancelled())
            return;

        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(userId);

        if(habbo == null)
            return;

        Message chatMessage = new Message(this.client.getHabbo().getHabboInfo().getId(), userId, event.message);
        Emulator.getGameServer().getGameClientManager().getClient(habbo).sendResponse(new FriendChatMessageComposer(chatMessage));
    }
}
