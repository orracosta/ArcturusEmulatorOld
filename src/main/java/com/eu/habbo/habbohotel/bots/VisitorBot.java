package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolRoomVisit;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VisitorBot extends Bot
{
    private static SimpleDateFormat formatDate;
    private boolean showedLog = false;
    private THashSet<ModToolRoomVisit> visits = new THashSet<ModToolRoomVisit>();

    public VisitorBot(ResultSet set) throws SQLException
    {
        super(set);
    }

    public VisitorBot(Bot bot)
    {
        super(bot);
    }

    @Override
    public void onUserSay(final RoomChatMessage message)
    {
        if(!this.showedLog)
        {
            if(message.getMessage().equalsIgnoreCase(Emulator.getTexts().getValue("generic.yes")))
            {
                this.showedLog = true;

                String visitMessage = Emulator.getTexts().getValue("bots.visitor.list").replace("%count%", this.visits.size() + "");

                String list = "";
                for(ModToolRoomVisit visit : this.visits)
                {
                    list += "\r";
                    list += visit.roomName + " ";
                    list += Emulator.getTexts().getValue("generic.time.at") + " ";
                    list += formatDate.format(new Date((visit.timestamp * 1000L)));
                }

                visitMessage = visitMessage.replace("%list%", list);

                this.talk(visitMessage);

                this.visits.clear();
            }
        }
    }

    public void onUserEnter(Habbo habbo)
    {
        if(!this.showedLog)
        {
            if(habbo.getHabboInfo().getCurrentRoom() != null)
            {
                this.visits = Emulator.getGameEnvironment().getModToolManager().getVisitsForRoom(habbo.getHabboInfo().getCurrentRoom(), 10, true, habbo.getHabboInfo().getLastOnline(), Emulator.getIntUnixTimestamp());

                if(this.visits.isEmpty())
                {
                    this.talk(Emulator.getTexts().getValue("bots.visitor.no_visits"));
                }
                else
                {
                    this.talk(Emulator.getTexts().getValue("bots.visitor.visits").replace("%count%", this.visits.size() + "").replace("%positive%", Emulator.getTexts().getValue("generic.yes")));
                }
            }
        }
    }

    public static void initialise()
    {
        formatDate = new SimpleDateFormat(Emulator.getConfig().getValue("bots.visitor.dateformat"));
    }

}
