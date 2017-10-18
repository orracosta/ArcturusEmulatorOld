package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class GuildForumManager
{
    private final TIntObjectMap<GuildForum> guildForums;

    public GuildForumManager()
    {
        this.guildForums = TCollections.synchronizedMap(new TIntObjectHashMap<GuildForum>());
    }

    public GuildForum getGuildForum(int guildId)
    {
        synchronized (this.guildForums)
        {
            GuildForum forum = this.guildForums.get(guildId);

            if (forum == null)
            {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

                if (guild != null && guild.hasForum())
                {
                    forum = new GuildForum(guild.getId());
                    this.guildForums.put(guild.getId(), forum);
                }
            }

            forum.updateLastRequested();
            return forum;
        }
    }

    public void clearInactiveForums()
    {
        int time = Emulator.getIntUnixTimestamp();

        TIntObjectIterator<GuildForum> guildForums = this.guildForums.iterator();
        for(int i = this.guildForums.size(); i-- > 0;)
        {
            try
            {
                guildForums.advance();
            }
            catch (NoSuchElementException e)
            {
                break;
            }

            if (time - guildForums.value().getLastRequestedTime() > 300)
            {
                this.guildForums.remove(guildForums.key());
            }
        }
    }

    public List<GuildForum> getGuildForums(Habbo habbo)
    {
        List<GuildForum> forums = new ArrayList<>();
        for (Integer i : habbo.getHabboStats().guilds)
        {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(i);

            if (guild != null && guild.hasForum())
            {
                forums.add(this.getGuildForum(i));
            }
        }

        return forums;
    }
}
