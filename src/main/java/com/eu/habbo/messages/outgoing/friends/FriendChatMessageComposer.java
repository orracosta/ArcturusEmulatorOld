package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class FriendChatMessageComposer extends MessageComposer
{
    private final Message message;

    public FriendChatMessageComposer(Message message)
    {
        this.message = message;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.FriendChatMessageComposer);

        this.response.appendInt32(message.getFromId());
        this.response.appendString(message.getMessage());
        this.response.appendInt32(Emulator.getIntUnixTimestamp() - message.getTimestamp());

        Emulator.getThreading().run(message);
        return this.response;
    }
}
