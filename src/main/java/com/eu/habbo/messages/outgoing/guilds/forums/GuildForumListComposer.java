package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.forums.GuildForum;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class GuildForumListComposer extends MessageComposer
{
    public final Guild guild;
    public final int viewMode;

    public GuildForumListComposer(Guild guild, int viewMode)
    {
        this.guild = guild;
        this.viewMode = viewMode;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildForumListComposer);
        this.response.appendInt32(this.viewMode);
        this.response.appendInt32(0);
        this.response.appendInt32(0);
        this.response.appendInt32(1); //Count...

        this.response.appendInt32(guild.getId()); //k._SafeStr_6864 = _arg_2._SafeStr_5878();   = guild_id
        this.response.appendString(guild.getName()); //k._name = _arg_2.readString();            = name
        this.response.appendString(guild.getDescription()); //k._SafeStr_5790 = _arg_2.readString();    = description
        this.response.appendString(guild.getBadge()); //k._icon = _arg_2.readString();            = icon
        this.response.appendInt32(1); //k._SafeStr_11338 = _arg_2._SafeStr_5878(); (?)
        this.response.appendInt32(0); //k._SafeStr_19191 = _arg_2._SafeStr_5878();  = rating
        this.response.appendInt32(1); //k._SafeStr_11328 = _arg_2._SafeStr_5878();  = total_messages
        this.response.appendInt32(1); //k._SafeStr_19192 = _arg_2._SafeStr_5878();  = new_messages
        this.response.appendInt32(0); //k._SafeStr_19193 = _arg_2._SafeStr_5878(); (?)
        this.response.appendInt32(1); //k._SafeStr_19194 = _arg_2._SafeStr_5878();  = last_author_id
        this.response.appendString("Admin"); //k._SafeStr_19195 = _arg_2.readString();   = last_author_name
        this.response.appendInt32(100); //k._SafeStr_19196 = _arg_2._SafeStr_5878();  = update_time

        return this.response;
    }
}