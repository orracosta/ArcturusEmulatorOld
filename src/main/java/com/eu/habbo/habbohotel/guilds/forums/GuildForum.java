package com.eu.habbo.habbohotel.guilds.forums;

import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

public class GuildForum implements ISerialize
{
    private final Guild guild;
    private final TIntObjectHashMap<GuildForumThread> threads;

    public GuildForum(Guild guild)
    {
        this.guild = guild;

        this.threads = new TIntObjectHashMap<GuildForumThread>();
    }

    public void createThread()
    {

    }

    public void deleteThread()
    {

    }

    public Guild getGuild()
    {
        return this.guild;
    }


    @Override
    public void serialize(ServerMessage message)
    {

    }

    public void serializeThreads(final ServerMessage message)
    {
        synchronized (this.threads)
        {
            message.appendInt32(this.threads.size());

            this.threads.forEachValue(new TObjectProcedure<GuildForumThread>()
            {
                @Override
                public boolean execute(GuildForumThread thread)
                {
                    thread.serialize(message);
                    return true;
                }
            });
        }
    }

    public enum ThreadState
    {
        /*
        public static const _SafeStr_11113:int = 0;
        public static const _SafeStr_11114:int = 1;
        public static const _SafeStr_11115:int = 10;
        public static const _SafeStr_9691:int = 20;
         */

        OPEN(0),
        CLOSED(1),
        HIDDEN_BY_ADMIN(10),
        HIDDEN_BY_STAFF(20);

        public final int state;

        ThreadState(int state)
        {
            this.state = state;
        }

        ThreadState fromValue(int state)
        {
            switch (state)
            {
                case 0: return OPEN;
                case 1: return CLOSED;
                case 10: return HIDDEN_BY_ADMIN;
                case 20: return HIDDEN_BY_STAFF;
            }

            return OPEN;
        }
    }
}