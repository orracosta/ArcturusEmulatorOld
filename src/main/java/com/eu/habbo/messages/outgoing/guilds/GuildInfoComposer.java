package com.eu.habbo.messages.outgoing.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.guilds.GuildRank;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 22-11-2014 22:07.
 */
public class GuildInfoComposer extends MessageComposer
{
    private final Guild guild;
    private final GameClient client;
    private final boolean newWindow;
    private final GuildMember member;

    public GuildInfoComposer(Guild guild, GameClient client, boolean newWindow, GuildMember member)
    {
        this.guild = guild;
        this.client = client;
        this.newWindow = newWindow;
        this.member = member;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GuildInfoComposer);
        this.response.appendInt32(this.guild.getId());
        this.response.appendBoolean(true);
        this.response.appendInt32(this.guild.getState().state);
        this.response.appendString(this.guild.getName());
        this.response.appendString(this.guild.getDescription());
        this.response.appendString(this.guild.getBadge());
        this.response.appendInt32(this.guild.getRoomId());
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.guild.getRoomId());
        this.response.appendString(room == null ? "No room found." : room.getName());
        //this.response.appendInt32(this.guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() ? 3 : (this.member == null ? 0 : (this.member.getRank().equals(GuildRank.MEMBER) || this.member.getRank().equals(GuildRank.MOD) ? 1 : (this.member.getRank().equals(GuildRank.REQUESTED) ? 2 : 0))));
        this.response.appendInt32(this.member == null ? 0 : (this.member.getRank().equals(GuildRank.MEMBER) ? 1 : (this.member.getRank().equals(GuildRank.REQUESTED) ? 2 : (this.member.getRank().equals(GuildRank.MOD) ? 3 : (this.member.getRank().equals(GuildRank.ADMIN) ? 4 : 0)))));
        this.response.appendInt32(this.guild.getMemberCount()); //Member count.
        this.response.appendBoolean(this.client.getHabbo().getHabboStats().guild == this.guild.getId()); //favorite group
        this.response.appendString(new SimpleDateFormat("dd-MM-yyyy").format(new Date(this.guild.getDateCreated() * 1000L)));
        this.response.appendBoolean(this.guild.getOwnerId() == this.client.getHabbo().getHabboInfo().getId());
        this.response.appendBoolean(this.member != null && (this.member.getRank().equals(GuildRank.ADMIN))); //Is admin. //this.member.getRank().equals(GuildRank.MOD) ||
        //Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.guild.getOwnerId());

        this.response.appendString(room != null ? room.getOwnerName() : "???");
        this.response.appendBoolean(this.newWindow);
        this.response.appendBoolean(this.guild.getRights() == 1); //User can place furni.
        this.response.appendInt32(this.guild.getRequestCount()); //Guild invites count.
        this.response.appendBoolean(true); //Unknown
        return this.response;
    }
}
