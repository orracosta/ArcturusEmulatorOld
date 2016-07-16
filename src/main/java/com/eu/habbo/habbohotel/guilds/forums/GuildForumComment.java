package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildForumComment implements ISerialize
{
    public final int id;
    public final int index;
    public final int userId;
    public final String userName;
    public final String look;
    public final int timestamp;
    public final String message;
    public GuildForum.ThreadState state = GuildForum.ThreadState.OPEN;
    public int adminId = 0;
    public String adminName = "";
    public int authorPostCount;

    public GuildForumComment(final ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.index = set.getInt("index");
        this.userId = set.getInt("user_id");
        this.userName = set.getString("username");
        this.look = set.getString("look");
        this.timestamp = set.getInt("timestamp");
        this.message = set.getString("message");
    }

    public void setAuthorPostCount(int authorPostCount)
    {
        this.authorPostCount = authorPostCount;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt32(this.id);               //_local_2.messageId = k._SafeStr_5878();       = message_id
        message.appendInt32(this.index);            //_local_2.messageIndex = k._SafeStr_5878();    = message_index
        message.appendInt32(this.userId);           //_local_2._SafeStr_11317 = k._SafeStr_5878();  = author_id
        message.appendString(this.userName);        //_local_2._SafeStr_6798 = k.readString();      = author_name
        message.appendString(this.look);            //_local_2._SafeStr_11319 = k.readString();     = author_look
        message.appendInt32(this.timestamp);        //_local_2._SafeStr_11164 = k._SafeStr_5878();  = creation_time
        message.appendString(this.message);         //_local_2._SafeStr_9526 = k.readString();      = message
        message.appendByte(this.state.state);       //_local_2.state = k.readByte();                = state
        message.appendInt32(this.adminId);          //_local_2._SafeStr_19188 = k._SafeStr_5878();  = admin_id
        message.appendString(this.adminName);       //_local_2._SafeStr_11326 = k.readString();     = admin_name
        message.appendInt32(0);                     //_local_2._SafeStr_19189 = k._SafeStr_5878();  = (UNUSED)
        message.appendInt32(this.authorPostCount);  //_local_2._SafeStr_11320 = k._SafeStr_5878();  = author_post_count
    }
}