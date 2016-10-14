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
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.forum.getGuild());

            //_SafeStr_3844.fillFromMessage : _SafeStr_3845
            {
                this.response.appendInt32(guild.getId()); //k._SafeStr_6864 = _arg_2._SafeStr_5878();   = guild_id
                this.response.appendString(guild.getName()); //k._name = _arg_2.readString();            = name
                this.response.appendString(guild.getDescription()); //k._SafeStr_5790 = _arg_2.readString();    = description
                this.response.appendString(guild.getBadge()); //k._icon = _arg_2.readString();            = icon
                this.response.appendInt32(0); //k._SafeStr_11338 = _arg_2._SafeStr_5878(); (?)
                this.response.appendInt32(0); //k._SafeStr_19191 = _arg_2._SafeStr_5878();  = rating
                this.response.appendInt32(this.forum.threadSize()); //k._SafeStr_11328 = _arg_2._SafeStr_5878();  = total_messages
                this.response.appendInt32(0); //k._SafeStr_19192 = _arg_2._SafeStr_5878();  = new_messages
                this.response.appendInt32(0); //k._SafeStr_19193 = _arg_2._SafeStr_5878(); (?)
                this.response.appendInt32(0); //k._SafeStr_19194 = _arg_2._SafeStr_5878();  = last_author_id
                this.response.appendString(""); //k._SafeStr_19195 = _arg_2.readString();   = last_author_name
                this.response.appendInt32(0); //k._SafeStr_19196 = _arg_2._SafeStr_5878();  = update_time
            }

            this.response.appendInt32(guild.canReadForum().state);
            this.response.appendInt32(guild.canPostMessages().state);
            this.response.appendInt32(guild.canPostThreads().state);
            this.response.appendInt32(guild.canModForum().state);
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