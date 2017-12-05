package com.eu.habbo.messages.incoming.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.SettingsState;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.guilds.forums.GuildForumDataComposer;

public class GuildForumUpdateSettingsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int guildId = packet.readInt();
        int canRead = packet.readInt();
        int postMessages = packet.readInt();
        int postThreads = packet.readInt();
        int modForum = packet.readInt();

        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

        if(guild == null || guild.getOwnerId() != this.client.getHabbo().getHabboInfo().getId())
            return;

        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(guildId);

        if(forum == null)
            return;

        guild.setReadForum(SettingsState.fromValue(canRead));
        guild.setPostMessages(SettingsState.fromValue(postMessages));
        guild.setPostThreads(SettingsState.fromValue(postThreads));
        guild.setModForum(SettingsState.fromValue(modForum));

        guild.needsUpdate = true;

        Emulator.getThreading().run(guild);

        this.client.sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FORUMS_FORUM_SETTINGS_UPDATED.key).compose());

        this.client.sendResponse(new GuildForumDataComposer(forum, this.client.getHabbo()));
    }
}