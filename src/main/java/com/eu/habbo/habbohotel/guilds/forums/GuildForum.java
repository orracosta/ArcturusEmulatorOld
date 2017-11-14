package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuildForum implements ISerialize {
    private final int guild;

    private int totalThreads;
    private final TIntObjectHashMap<GuildForumThread> threads;
    private int lastRequested = Emulator.getIntUnixTimestamp();

    public GuildForum(int guild) {
        this.guild = guild;

        this.threads = new TIntObjectHashMap<GuildForumThread>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT author.username as author_name, author.look as look, COALESCE(admin.username, '') as admin_name, guilds_forums.id as thread_id, 0 as row_number, guilds_forums.* FROM guilds_forums " +
                "INNER JOIN users AS author ON author.id = user_id " +
                "LEFT JOIN users AS admin ON guilds_forums.admin_id = admin.id " +
                "WHERE guild_id = ?")) {
            statement.setInt(1, this.guild);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    this.threads.put(set.getInt("id"), new GuildForumThread(set));
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public GuildForumComment getLastComment() {
        if (!this.threads.valueCollection().isEmpty()) {
            GuildForumThread thread = Collections.max(this.threads.valueCollection(), Comparator.comparing(GuildForumThread::getLastCommentTimestamp));

            if (thread != null && thread.comments.size() > 0) {
                return thread.comments.get(thread.comments.size() - 1);
            }
        }

        return null;
    }

    public List<GuildForumThread> getThreads() {
        return new ArrayList<>(this.threads.valueCollection());
    }

    public List<GuildForumThread> getThreadsByAuthor(int userId) {
        return this.threads.valueCollection().stream().filter(p -> p.getAuthorId() == userId).collect(Collectors.toList());
    }

    public GuildForumThread getThread(int threadId) {
        return threads.get(threadId);
    }

    public GuildForumThread createThread(Habbo habbo, String subject, String message) {
        int timestamp = Emulator.getIntUnixTimestamp();
        GuildForumThread thread = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guilds_forums (guild_id, user_id, subject, message, timestamp) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, this.guild);
            statement.setInt(2, habbo.getClient().getHabbo().getHabboInfo().getId());
            statement.setString(3, subject);
            statement.setString(4, message);
            statement.setInt(5, timestamp);
            statement.execute();

            try (ResultSet set = statement.getGeneratedKeys()) {
                if (set.next()) {
                    thread = new GuildForumThread(habbo, set.getInt(1), this.guild, subject, message, timestamp);
                    this.threads.put(set.getInt(1),  //Thread id
                            thread);
                }
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }

        return thread;
    }

    //TODO:
    /*
        Verwijderen door guild admin -> state naar HIDDEN_BY_ADMIN
            Guild admins moeten nog wel forum kunnen bekijken.
        Verwijderen door staff -> state naar HIDDEN_BY_STAFF
            Guild admins kunnen de thread niet zien. Staff wel.

        Thread wordt nooit uit de database verwijderd, alleen als de groep verwijderd wordt.
     */

    public void hideThread(int threadId) {
        this.threads.get(threadId).setState(ThreadState.HIDDEN_BY_ADMIN);
    }

    public int getGuild() {
        return this.guild;
    }

    int getLastRequestedTime() {
        return this.lastRequested;
    }

    @Override
    public void serialize(ServerMessage message) {

    }

    public void serializeThreads(final ServerMessage message) {
        synchronized (this.threads) {
            message.appendInt(this.threads.size());

            this.threads.forEachValue(new TObjectProcedure<GuildForumThread>() {
                @Override
                public boolean execute(GuildForumThread thread) {
                    thread.serialize(message);
                    return true;
                }
            });
        }
    }

    public int threadSize() {
        synchronized (this.threads) {
            return this.threads.size();
        }
    }

    void updateLastRequested() {
        this.lastRequested = Emulator.getIntUnixTimestamp();
    }

    public enum ThreadState {
        CLOSED(0),
        OPEN(1),
        HIDDEN_BY_ADMIN(10), //DELETED
        HIDDEN_BY_STAFF(20);

        public final int state;

        ThreadState(int state) {
            this.state = state;
        }

        public static ThreadState fromValue(int state) {
            switch (state) {
                case 0:
                    return CLOSED;
                case 1:
                    return OPEN;
                case 10:
                    return HIDDEN_BY_ADMIN;
                case 20:
                    return HIDDEN_BY_STAFF;
            }

            return OPEN;
        }
    }

    public void serializeUserForum(ServerMessage response, Habbo habbo) {
        Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);
        GuildForum forum = Emulator.getGameEnvironment().getGuildForumManager().getGuildForum(this.guild);

        Integer amountOfComments = forum.getThreads().stream().map(GuildForumThread::getAmountOfComments).mapToInt(Integer::intValue).sum();

        response.appendInt(guild.getId());

        response.appendString(guild.getName());
        response.appendString(guild.getDescription());
        response.appendString(guild.getBadge());

        response.appendInt(0);
        response.appendInt(0); //Rating
        response.appendInt(amountOfComments);

        response.appendInt(0); //New Messages

        GuildForumComment comment = this.getLastComment();

        if (comment != null) {
            response.appendInt(comment.getThreadId());
            response.appendInt(comment.getUserId());
            response.appendString(comment.getUserName());
            response.appendInt(Emulator.getIntUnixTimestamp() - comment.getTimestamp());

            return;
        }

        response.appendInt(-1);
        response.appendInt(-1);
        response.appendString("");
        response.appendInt(0);
    }
}