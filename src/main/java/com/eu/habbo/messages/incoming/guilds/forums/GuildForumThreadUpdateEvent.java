package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.guilds.forums.ThreadUpdatedMessageComposer;

public class GuildForumThreadUpdateEvent extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int groupId = this.packet.readInt();
        int threadId = this.packet.readInt();
        boolean isPinned = this.packet.readBoolean();
        boolean isLocked = this.packet.readBoolean();

        this.client.sendResponse(new ThreadUpdatedMessageComposer(Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(groupId), threadId, this.client.getHabbo(), isPinned, isLocked));
    }
}