package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

public class GuildForumThread implements ISerialize
{
    public final TIntObjectHashMap<GuildForumComment> comments;

    public GuildForumThread(TIntObjectHashMap<GuildForumComment> comments)
    {
        this.comments = comments;
    }

    public int getGuildId()
    {
        return 0;
    }

    public int getId()
    {
        return 0;
    }

    public int getStartIndex()
    {
        return 0;
    }

    public int getCommentsSize()
    {
        return this.comments.size();
    }

    public void serializeComments(final ServerMessage message)
    {
        synchronized (this.comments)
        {
            message.appendInt32(this.comments.size());
            this.comments.forEachValue(new TObjectProcedure<GuildForumComment>()
            {
                @Override
                public boolean execute(GuildForumComment comment)
                {
                    comment.serialize(message);
                    return true;
                }
            });
        }
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt32(0); //_local_2.threadId = k._SafeStr_5878();
        message.appendInt32(0); //_local_2._SafeStr_11333 = k._SafeStr_5878(); = thread_author_id
        message.appendString(""); //_local_2._SafeStr_11334 = k.readString(); = thread_author_name
        message.appendString(""); //_local_2.header = k.readString(); = header
        message.appendBoolean(false); //_local_2._SafeStr_11331 = k.readBoolean(); = pinned
        message.appendBoolean(false); //_local_2._SafeStr_5801 = k.readBoolean(); = locked
        message.appendInt32(0); //_local_2._SafeStr_11164 = k._SafeStr_5878(); = creation_time
        message.appendInt32(0); //_local_2._SafeStr_11239 = k._SafeStr_5878(); = total_messages
        message.appendInt32(0); //_local_2._SafeStr_11260 = k._SafeStr_5878(); = unread_messages(?)
        message.appendInt32(0); //_local_2._SafeStr_11242 = k._SafeStr_5878(); = Something message count related.
        message.appendInt32(0); //_local_2._SafeStr_11188 = k._SafeStr_5878(); = last_author_id
        message.appendString(""); //_local_2._SafeStr_11189 = k.readString(); = last_author_name
        message.appendInt32(0); //_local_2._SafeStr_11190 = k._SafeStr_5878(); = update_time
        message.appendByte(0); //_local_2.state = k.readByte(); = state
        message.appendInt32(0); //_local_2._SafeStr_19188 = k._SafeStr_5878(); //UNUSED?
        message.appendString(""); //_local_2._SafeStr_11326 = k.readString(); = admin_name
        message.appendInt32(0); //_local_2._SafeStr_19214 = k._SafeStr_5878(); //UNUSED?
    }
}