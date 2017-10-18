package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuildForumThread implements ISerialize, Runnable
{
    private final int threadId;
    private final int guildId;
    private final int authorId;
    private final String authorName;
    private final String subject;
    private final String message;
    private GuildForum.ThreadState state;
    private final int timestamp;
    private boolean pinned = false;
    private boolean locked = false;

    private int lastAuthorId = 0;
    private String lastAuthorName;
    private int lastCommentTimestamp = 0;
    private int adminId;
    private String adminName = "";

    //public final TIntObjectHashMap<GuildForumComment> comments;
    public final List<GuildForumComment> comments;

    public GuildForumThread(Habbo habbo, int threadId, int guildId, String subject, String message, int timestamp)
    {
        this.threadId = threadId;
        this.guildId = guildId;
        this.authorId = habbo.getHabboInfo().getId();
        this.authorName = habbo.getHabboInfo().getUsername();
        this.subject = subject;
        this.message = message;
        this.state = GuildForum.ThreadState.OPEN;
        this.timestamp = timestamp;
        this.lastAuthorId = this.authorId;
        this.lastAuthorName = this.authorName;
        this.lastCommentTimestamp = this.timestamp;

        //this.comments = new TIntObjectHashMap<GuildForumComment>();
        this.comments = new ArrayList<GuildForumComment>();
    }

    //Via de database inladen;
    public GuildForumThread(ResultSet set) throws SQLException
    {
        this.threadId = set.getInt("id");
        this.guildId = set.getInt("guild_id");
        this.authorId = set.getInt("user_id");
        this.authorName = set.getString("author_name");
        this.subject = set.getString("subject");
        this.message = set.getString("message");
        this.state = GuildForum.ThreadState.valueOf(set.getString("state"));
        this.timestamp = set.getInt("timestamp");
        this.pinned = set.getString("pinned").equals("1");
        this.locked = set.getString("locked").equals("1");
        this.adminId = set.getInt("admin_id");
        this.adminName = set.getString("admin_name");


        this.lastAuthorId = this.authorId;
        this.lastAuthorName = this.authorName;
        this.lastCommentTimestamp = this.timestamp;

        this.comments = new ArrayList<GuildForumComment>();
        this.addComment(new GuildForumComment(set, 0));


        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT " +
                "author.username AS author_name, " +
                "COALESCE(admin.username, '') as admin_name, " +
                "author.look, " +
                "guilds_forums_comments.*" +
                "FROM guilds_forums_comments " +
                "INNER JOIN users AS author ON guilds_forums_comments.user_id = author.id " +
                "LEFT JOIN users AS admin  ON guilds_forums_comments.admin_id = admin.id " +
                "WHERE thread_id = ? " +
                "ORDER BY id ASC"))
        {
            statement.setInt(1, this.threadId);
            try (ResultSet commentSet = statement.executeQuery())
            {
                int index = 1;
                while (commentSet.next())
                {
                    if (!commentSet.isLast())
                    {
                        this.comments.add(new GuildForumComment(commentSet, index));
                    }
                    else
                    {
                        this.addComment(new GuildForumComment(commentSet, index));
                    }

                    ++index;
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void addComment(GuildForumComment comment)
    {
        synchronized (this.comments)
        {
            if (comment.getIndex() == -1)
            {
                if (!this.comments.isEmpty())
                {
                    GuildForumComment previousComment = this.comments.get(this.comments.size() - 1);

                    if (previousComment != null)
                    {
                        comment.setIndex(previousComment.getIndex() + 1);
                    }
                }
            }

            this.comments.add(comment);
        }

        this.lastAuthorId = comment.getUserId();
        this.lastAuthorName = comment.getUserName();
        this.lastCommentTimestamp = comment.getTimestamp();
    }

    public GuildForumComment addComment(Habbo habbo, String message)
    {
        int commentId = -1;

        GuildForumComment comment = new GuildForumComment(this.guildId, this.threadId, habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername(), habbo.getHabboInfo().getLook(), message);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO guilds_forums_comments (thread_id, user_id, timestamp, message) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, this.threadId);
            statement.setInt(2, habbo.getHabboInfo().getId());
            int nowTimestamp = Emulator.getIntUnixTimestamp();
            statement.setInt(3, nowTimestamp);
            statement.setString(4, message);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys())
            {
                if (set.next())
                {
                    commentId = set.getInt(1);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if (commentId >= 0)
        {
            comment.setId(commentId);
            addComment(comment);

            return comment;
        }

        return null;
    }

    public GuildForumComment getComment(int id)
    {
        synchronized (this.comments)
        {
            return this.comments.get(id);
        }
    }

    public List<GuildForumComment> getComments(int page, int limit)
    {
        synchronized (this.comments)
        {
            return this.comments.subList(page,  (page + limit) > this.comments.size() ? this.comments.size() : (page + limit));
        }
    }

    public int getId()
    {
        return this.threadId;
    }

    public int getGuildId()
    {
        return this.guildId;
    }

    public int getCommentsSize()
    {
        return this.comments.size();
    }

    public String getSubject()
    {
        return this.subject;
    }

    public int getThreadId()
    {
        return this.threadId;
    }

    public int getAuthorId()
    {
        return this.authorId;
    }

    public String getAuthorName()
    {
        return this.authorName;
    }

    public String getMessage()
    {
        return this.message;
    }

    public GuildForum.ThreadState getState()
    {
        return this.state;
    }

    public void setState(GuildForum.ThreadState state)
    {
        this.state = state;
    }

    public void setAdminId(int adminId)
    {
        this.adminId = adminId;
    }

    public void setAdminName(String adminName)
    {
        this.adminName = adminName;
    }

    public void serializeComments(final ServerMessage message, int index, int limit)
    {
        List<GuildForumComment> comments;

        synchronized (this.comments)
        {
            comments = this.getComments(index, limit);
        }

        if (comments != null)
        {
            message.appendInt(comments.size());
            for (GuildForumComment comment : comments)
            {
                comment.serialize(message);
            }
        }
    }

    @Override
    public void serialize(ServerMessage message)
    {
        int nowTimestamp = Emulator.getIntUnixTimestamp();
        message.appendInt(this.threadId); //_local_2.threadId = k._SafeStr_5878();
        message.appendInt(this.authorId); //_local_2._SafeStr_11333 = k._SafeStr_5878(); = thread_author_id
        message.appendString(this.authorName); //_local_2._SafeStr_11334 = k.readString(); = thread_author_name
        message.appendString(this.subject); //_local_2.header = k.readString(); = look
        message.appendBoolean(this.pinned); //_local_2._SafeStr_11331 = k.readBoolean(); = pinned
        message.appendBoolean(this.locked); //_local_2._SafeStr_5801 = k.readBoolean(); = locked
        message.appendInt(nowTimestamp - this.timestamp); //_local_2._SafeStr_11164 = k._SafeStr_5878(); = creation_time
        message.appendInt(this.getCommentsSize()); //_local_2._SafeStr_11239 = k._SafeStr_5878(); = total_messages
        message.appendInt(0); //_local_2._SafeStr_11260 = k._SafeStr_5878(); = unread_messages(?)
        message.appendInt(1); //_local_2._SafeStr_11242 = k._SafeStr_5878(); = Something message count related.
        message.appendInt(this.lastAuthorId); //_local_2._SafeStr_11188 = k._SafeStr_5878(); = last_author_id
        message.appendString(this.lastAuthorName); //_local_2._SafeStr_11189 = k.readString(); = last_author_name
        message.appendInt(nowTimestamp - this.lastCommentTimestamp); //_local_2._SafeStr_11190 = k._SafeStr_5878(); = update_time, seconds ago
        message.appendByte(this.state.state); //_local_2.state = k.readByte(); = state
        message.appendInt(this.adminId); //_local_2._SafeStr_19188 = k._SafeStr_5878(); = admin id
        message.appendString(this.adminName); //_local_2._SafeStr_11326 = k.readString(); = admin_name
        message.appendInt(this.threadId); //_local_2._SafeStr_19214 = k._SafeStr_5878(); //UNUSED?
    }


    @Override
    public void run()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE guilds_forums SET message = ?, state = ?, pinned = ?, locked = ?, admin_id = ? WHERE id = ?"))
        {
            statement.setString(1, this.message);
            statement.setString(2, this.state.name());
            statement.setString(3, this.pinned ? "1" : "0");
            statement.setString(4, this.locked ? "1" : "0");
            statement.setInt(5, this.adminId);
            statement.setInt(6, this.getId());
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}