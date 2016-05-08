package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class GuildMembersComposer extends MessageComposer
{
    private final ArrayList<GuildMember> members;
    private final Guild guild;
    private final Habbo session;
    private final int pageId;
    private final int level;

    public GuildMembersComposer(Guild guild, ArrayList<GuildMember> members, Habbo session, int pageId, int level)
    {
        this.guild = guild;
        this.members = members;
        this.session = session;
        this.pageId = pageId;
        this.level = level;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildMembersComposer);
        this.response.appendInt32(this.guild.getId());
        this.response.appendString(this.guild.getName());
        this.response.appendInt32(this.guild.getRoomId());
        this.response.appendString(this.guild.getBadge());
        this.response.appendInt32(this.members.size());
        this.response.appendInt32(this.members.size());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        for(GuildMember member : this.members)
        {
            this.response.appendInt32(member.getRank().type);
            this.response.appendInt32(member.getUserId());
            this.response.appendString(member.getUsername());
            this.response.appendString(member.getLook());
            this.response.appendString(member.getRank().type < 3 && member.getRank().type > 0 ? cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) : "");
        }

        this.response.appendBoolean(this.guild.getOwnerId() == this.session.getHabboInfo().getId()); //Is owner
        this.response.appendInt32(14);
        this.response.appendInt32(this.pageId);
        this.response.appendInt32(this.level);
        this.response.appendString("");
        return this.response;
    }
}
