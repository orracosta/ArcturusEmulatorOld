package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

public class GuildForumManager {
    private final TIntObjectMap<GuildForum> guildForums;

    public GuildForumManager() {
        this.guildForums = TCollections.synchronizedMap(new TIntObjectHashMap<GuildForum>());
    }

    public GuildForum getGuildForum(int guildId) {
        synchronized (this.guildForums) {
            GuildForum forum = this.guildForums.get(guildId);

            if (forum == null) {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

                if (guild != null && guild.hasForum()) {
                    forum = new GuildForum(guild.getId());
                    this.guildForums.put(guild.getId(), forum);
                }
            }

            if(forum != null) {
                forum.updateLastRequested();
                return forum;
            }

            return null;
        }
    }

    public void clearInactiveForums() {
        List<Integer> toRemove = new ArrayList<Integer>();
        TIntObjectIterator<GuildForum> guildForums = this.guildForums.iterator();
        for (int i = this.guildForums.size(); i-- > 0; ) {
            try {
                guildForums.advance();
            }
            catch (NoSuchElementException | ConcurrentModificationException e) {
                break;
            }

            if (guildForums.value().getLastRequestedTime() < Emulator.getIntUnixTimestamp() - 300) {
                toRemove.add(guildForums.key());
            }

            for (Integer j : toRemove) {
                this.guildForums.remove(j);
            }
        }
    }

    public List<GuildForum> getGuildForums(Habbo habbo) {
        List<GuildForum> forums = new ArrayList<>();

        for (Integer i : habbo.getHabboStats().guilds) {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(i);

            if (guild != null && guild.hasForum()) {
                forums.add(this.getGuildForum(i));
            }
        }

        return forums;
    }

    public List<GuildForum> getAllForums() {
        List<GuildForum> forums = new ArrayList<>();

        for (Guild guild : Emulator.getGameEnvironment().getGuildManager().getAllGuilds()) {
            if (guild != null && guild.hasForum()) {
                forums.add(this.getGuildForum(guild.getId()));
            }
        }

        return forums;
    }

    public static final Comparator SORT_ACTIVE = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {

            if (!(o1 instanceof GuildForum && o2 instanceof GuildForum))
                return 0;

            return ((GuildForum) o2).getLastComment().getTimestamp() - ((GuildForum) o1).getLastComment().getTimestamp();
        }
    };

    public static final Comparator SORT_VISITED = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {

            if (!(o1 instanceof GuildForum && o2 instanceof GuildForum))
                return 0;

            return ((GuildForum) o2).getLastRequestedTime() - ((GuildForum) o1).getLastRequestedTime();
        }
    };

    public List<GuildForum> getAllForumsByActive() {
        List<GuildForum> forums = new ArrayList<>();

        for (Guild guild : Emulator.getGameEnvironment().getGuildManager().getAllGuilds()) {
            if (guild != null && guild.hasForum()) {
                forums.add(this.getGuildForum(guild.getId()));
            }
        }

        Collections.sort(forums, this.SORT_ACTIVE);

        return forums;
    }

    public List<GuildForum> getAllForumsByVisited() {
        List<GuildForum> forums = new ArrayList<>();

        for (Guild guild : Emulator.getGameEnvironment().getGuildManager().getAllGuilds()) {
            if (guild != null && guild.hasForum()) {
                forums.add(this.getGuildForum(guild.getId()));
            }
        }

        Collections.sort(forums, this.SORT_VISITED);

        return forums;
    }
}
