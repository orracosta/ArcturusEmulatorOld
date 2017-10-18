package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.Emulator;
import gnu.trove.map.hash.THashMap;

import java.sql.*;

public class HallOfFame
{
    /**
     * Hall of Fame winners are in here.
     */
    private final THashMap<Integer, HallOfFameWinner> winners = new THashMap<Integer, HallOfFameWinner>();

    /**
     * The name of the competition.
     */
    private static String competitionName;

    public HallOfFame()
    {
        setCompetitionName("xmasRoomComp");

        reload();
    }

    /**
     * Reloads the HoF
     */
    public void reload()
    {
        this.winners.clear();

        synchronized (this.winners)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery(Emulator.getConfig().getValue("hotelview.halloffame.query")))
            {
                while (set.next())
                {
                    HallOfFameWinner winner = new HallOfFameWinner(set);
                    this.winners.put(winner.getId(), winner);
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public THashMap<Integer, HallOfFameWinner> getWinners()
    {
        return this.winners;
    }

    public String getCompetitionName()
    {
        return this.competitionName;
    }

    void setCompetitionName(String name)
    {
        this.competitionName = name;
    }
}
