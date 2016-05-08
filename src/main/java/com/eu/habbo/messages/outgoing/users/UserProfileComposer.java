package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 26-8-2014 12:24.
 */
public class UserProfileComposer extends MessageComposer {

    private final HabboInfo habboInfo;
    private Habbo habbo;

    public UserProfileComposer(HabboInfo habboInfo)
    {
        this.habboInfo = habboInfo;
    }

    public UserProfileComposer(Habbo habbo)
    {
        this.habbo = habbo;
        this.habboInfo = habbo.getHabboInfo();
    }

    @Override
    public ServerMessage compose() {

        if(this.habboInfo == null)
            return null;

        this.response.init(Outgoing.UserProfileComposer);

        this.response.appendInt32(this.habboInfo.getId());
        this.response.appendString(this.habboInfo.getUsername());
        this.response.appendString(this.habboInfo.getLook());
        this.response.appendString(this.habboInfo.getMotto());
        this.response.appendString(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(this.habboInfo.getAccountCreated() * 1000L)));
        this.response.appendInt32(this.habboInfo.getAchievementScore());
        this.response.appendInt32(Messenger.getFriendCount(this.habboInfo.getId()));
        this.response.appendBoolean(false);
        this.response.appendBoolean(false);
        this.response.appendBoolean(this.habboInfo.isOnline());

        if(habbo != null)
        {
            THashSet<Guild> guilds = new THashSet<Guild>();
            for (int i : this.habbo.getHabboStats().guilds)
            {
                if(i == 0)
                    break;

                guilds.add(Emulator.getGameEnvironment().getGuildManager().getGuild(i));
            }

            this.response.appendInt32(guilds.size());
            for(Guild guild : guilds)
            {
                this.response.appendInt32(guild.getId());
                this.response.appendString(guild.getName());
                this.response.appendString(guild.getBadge());
                this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorOne()).valueA);
                this.response.appendString(Emulator.getGameEnvironment().getGuildManager().getSymbolColor(guild.getColorTwo()).valueA);
                this.response.appendBoolean(guild.getId() == this.habbo.getHabboStats().guild);
                this.response.appendInt32(0);
                this.response.appendBoolean(guild.getOwnerId() == this.habbo.getHabboInfo().getId());
            }
        }
        else
            this.response.appendInt32(0); //Guild Size!

        this.response.appendInt32(Emulator.getIntUnixTimestamp() - this.habboInfo.getLastOnline()); //Secs ago.
        this.response.appendBoolean(true);

        return this.response;
    }
}
