package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;

public class GuildForumManager {
    private final TIntObjectMap<GuildForum> guildForums;

    public void addGuildForum(int guildId) {
        GuildForum forum = new GuildForum(guildId);

        this.guildForums.put(guildId, forum);
    }

    public GuildForumManager() {
        this.guildForums = TCollections.synchronizedMap(new TIntObjectHashMap<GuildForum>());
    }

    public GuildForum getGuildForum(int guildId) {
        synchronized (this.guildForums) {
            GuildForum forum = this.guildForums.get(guildId);

            if (forum == null) {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(guildId);

                if (guild != null && guild.hasForum()) {

                    forum = new GuildForum(guildId);

                    this.guildForums.put(guildId, forum);
                }
            }

            if (forum != null) {

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
            } catch (NoSuchElementException | ConcurrentModificationException e) {
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
            forums.add(this.getGuildForum(i));
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

    public List<GuildForum> getAllForumsWithPosts() {
        List<GuildForum> forums = new ArrayList<>();

        for (Guild guild : Emulator.getGameEnvironment().getGuildManager().getAllGuilds()) {
            if (guild != null && guild.hasForum()) {
                GuildForum forum = this.getGuildForum(guild.getId());

                if (forum.getLastComment() == null)
                    continue;

                forums.add(forum);
            }
        }

        return forums;
    }

    private static final Comparator<GuildForum> SORT_ACTIVE = new Comparator<GuildForum>() {
        @Override
        public int compare(GuildForum o1, GuildForum o2) {

            if (o2.getLastComment() == null || o2.getLastComment().getTimestamp() <= 0)
                return 0;

            if (o1.getLastComment() == null || o1.getLastComment().getTimestamp() <= 0)
                return 0;

            return o2.getLastComment().getTimestamp() - o1.getLastComment().getTimestamp();
        }
    };

    private static final Comparator<GuildForum> SORT_VISITED = new Comparator<GuildForum>() {
        @Override
        public int compare(GuildForum o1, GuildForum o2) {

            if (o2.getLastRequestedTime() <= 0 || o1.getLastRequestedTime() <= 0)
                return 0;

            return o2.getLastRequestedTime() - o1.getLastRequestedTime();
        }
    };

    public List<GuildForum> getAllForumsByActive() {
        List<GuildForum> forums = this.getAllForumsWithPosts();

        forums.sort(SORT_ACTIVE);

        return forums;
    }

    public List<GuildForum> getAllForumsByVisited() {
        List<GuildForum> forums = this.getAllForumsWithPosts();

        forums.sort(SORT_VISITED);

        return forums;
    }
}
