package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolRoomVisit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;
import java.util.Calendar;
import java.util.TimeZone;

public class ModToolUserRoomVisitsComposer extends MessageComposer
{
    private final Habbo habbo;
    private final THashSet<ModToolRoomVisit> roomVisits;

    public ModToolUserRoomVisitsComposer(Habbo habbo, THashSet<ModToolRoomVisit> roomVisits)
    {
        this.habbo = habbo;
        this.roomVisits = roomVisits;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolUserRoomVisitsComposer);

        this.response.appendInt(this.habbo.getHabboInfo().getId());
        this.response.appendString(this.habbo.getHabboInfo().getUsername());
        this.response.appendInt(this.roomVisits.size());

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        for(ModToolRoomVisit visit : this.roomVisits)
        {
            cal.setTimeInMillis(visit.timestamp * 1000);
            this.response.appendInt(visit.roomId);
            this.response.appendString(visit.roomName);
            this.response.appendInt(cal.get(Calendar.HOUR));
            this.response.appendInt(cal.get(Calendar.MINUTE));
        }

        return this.response;
    }
}
