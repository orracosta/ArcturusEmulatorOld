package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;

public class RoomUsersGuildBadgesComposer extends MessageComposer
{
    public final THashMap<Integer, String> guildBadges;

    public RoomUsersGuildBadgesComposer(THashMap<Integer, String> guildBadges)
    {
        this.guildBadges = guildBadges;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUsersGuildBadgesComposer);
        this.response.appendInt(this.guildBadges.size());

        this.guildBadges.forEachEntry(new TObjectObjectProcedure<Integer, String>()
        {
            @Override
            public boolean execute(Integer guildId, String badge)
            {
                response.appendInt(guildId);
                response.appendString(badge);
                return true;
            }
        });
        return this.response;
    }
}