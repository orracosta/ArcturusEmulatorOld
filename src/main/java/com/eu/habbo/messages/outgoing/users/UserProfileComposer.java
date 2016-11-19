package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProfileComposer extends MessageComposer
{
    private final HabboInfo habboInfo;
    private Habbo habbo;
    private GameClient viewer;

    public UserProfileComposer(HabboInfo habboInfo, GameClient viewer)
    {
        this.habboInfo = habboInfo;
        this.viewer = viewer;
    }

    public UserProfileComposer(Habbo habbo, GameClient viewer)
    {
        this.habbo = habbo;
        this.habboInfo = habbo.getHabboInfo();
        this.viewer = viewer;
    }

    @Override
    public ServerMessage compose()
    {
        if(this.habboInfo == null)
            return null;

        this.response.init(Outgoing.UserProfileComposer);

        this.response.appendInt32(this.habboInfo.getId());
        this.response.appendString(this.habboInfo.getUsername());
        this.response.appendString(this.habboInfo.getLook());
        this.response.appendString(this.habboInfo.getMotto());
        this.response.appendString(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(this.habboInfo.getAccountCreated() * 1000L)));
        this.response.appendInt32(this.habbo != null ? this.habbo.getHabboStats().getAchievementScore() : 0);
        this.response.appendInt32(Messenger.getFriendCount(this.habboInfo.getId()));
        this.response.appendBoolean(viewer.getHabbo().getMessenger().getFriends().containsKey(this.habboInfo.getId())); //Friend
        this.response.appendBoolean(Messenger.friendRequested(this.viewer.getHabbo().getHabboInfo().getId(), this.habboInfo.getId())); //Friend Request Send
        this.response.appendBoolean(this.habboInfo.isOnline());

        List<Guild> guilds = new ArrayList<Guild>();
        if(habbo != null)
        {
            for (int i : this.habbo.getHabboStats().guilds)
            {
                if (i == 0)
                    break;

                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(i);

                if (guild != null)
                {
                    guilds.add(guild);
                }
                else
                {
                    this.habbo.getHabboStats().removeGuild(i);
                }
            }
        }
        else
        {
            guilds = Emulator.getGameEnvironment().getGuildManager().getGuilds(this.habboInfo.getId());
        }

        this.response.appendInt32(guilds.size());
        for(Guild guild : guilds)
        {
            this.response.appendInt32(guild.getId());
            this.response.appendString(guild.getName());
            this.response.appendString(guild.getBadge());
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorOne()).valueA);
            this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorTwo()).valueA);
            this.response.appendBoolean(habbo != null && guild.getId() == this.habbo.getHabboStats().guild);
            this.response.appendInt32(guild.getOwnerId());
            this.response.appendBoolean(guild.getOwnerId() == this.habboInfo.getId());
        }

        this.response.appendInt32(Emulator.getIntUnixTimestamp() - this.habboInfo.getLastOnline()); //Secs ago.
        this.response.appendBoolean(true);

        return this.response;
    }
}
