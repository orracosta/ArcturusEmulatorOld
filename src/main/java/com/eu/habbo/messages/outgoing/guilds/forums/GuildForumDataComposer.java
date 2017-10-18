package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GuildForumDataComposer extends MessageComposer
{
    public final GuildForum forum;
    public Habbo habbo;

    public GuildForumDataComposer(GuildForum forum, Habbo habbo)
    {
        this.forum = forum;
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        //Possibly related to settings.
        this.response.init(Outgoing.GuildForumDataComposer);
        //_SafeStr_3845._SafeStr_19178(k)
        {

            this.forum.serializeUserForum(this.response, this.habbo);

            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.forum.getGuild());

            this.response.appendInt(guild.canReadForum().state);
            this.response.appendInt(guild.canPostMessages().state);
            this.response.appendInt(guild.canPostThreads().state);
            this.response.appendInt(guild.canModForum().state);
            this.response.appendString(""); //_local_2._SafeStr_19197 = k.readString();
            this.response.appendString(""); //_local_2._SafeStr_19198 = k.readString(); = member
            this.response.appendString(guild.getOwnerId()== this.habbo.getClient().getHabbo().getHabboInfo().getId() ? "" : "not_owner"); //_local_2._SafeStr_19199 = k.readString(); = owner
            this.response.appendString(guild.getOwnerId()== this.habbo.getClient().getHabbo().getHabboInfo().getId() ? "" : "not_admin"); //_local_2._SafeStr_19200 = k.readString(); = admin
            this.response.appendString(""); //_local_2._SafeStr_19201 = k.readString(); = citizen
            this.response.appendBoolean(guild.getOwnerId() == this.habbo.getClient().getHabbo().getHabboInfo().getId()); //;_local_2._SafeStr_19202 = k.readBoolean(); = acces forum settings
            this.response.appendBoolean(false); //_local_2._SafeStr_19203 = k.readBoolean();
        }
        return this.response;
    }
}