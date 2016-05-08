package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 22-11-2014 22:48.
 */
public class GuildManageComposer extends MessageComposer
{
    private final Guild guild;

    public GuildManageComposer(Guild guild)
    {
        this.guild = guild;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildManageComposer);
        this.response.appendInt32(1);
        this.response.appendInt32(1);
        this.response.appendString("Room Name");
        this.response.appendBoolean(false);
        this.response.appendBoolean(true);
        this.response.appendInt32(this.guild.getId());
        this.response.appendString(this.guild.getName());
        this.response.appendString(this.guild.getDescription());
        this.response.appendInt32(this.guild.getRoomId());
        this.response.appendInt32(this.guild.getColorOne());
        this.response.appendInt32(this.guild.getColorTwo());
        this.response.appendInt32(this.guild.getState().state);
        this.response.appendInt32(this.guild.getRights());
        this.response.appendBoolean(false);
        this.response.appendString("");
        this.response.appendInt32(5);
        String badge = this.guild.getBadge();
        badge = badge.replace("b", "");
        String[] data = badge.split("s");
        int req = 5 - data.length;
        int i = 0;
        for(String s : data)
        {
            this.response.appendInt32((s.length() > 5 ? Integer.parseInt(s.substring(0, 3)) : Integer.parseInt(s.substring(0, 2))));
            this.response.appendInt32((s.length() > 5 ? Integer.parseInt(s.substring(3, 5)) : Integer.parseInt(s.substring(2, 4))));

            if(s.length() < 5)
                this.response.appendInt32(0);
            else if(s.length() > 6)
                this.response.appendInt32(Integer.parseInt(s.substring(5, 6)));
            else
                this.response.appendInt32(Integer.parseInt(s.substring(4, 5)));
        }
        while(i != req)
        {
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            this.response.appendInt32(0);
            ++i;
        }
        this.response.appendString(this.guild.getBadge());
        this.response.appendInt32(this.guild.getMemberCount());
        return this.response;
    }
}
