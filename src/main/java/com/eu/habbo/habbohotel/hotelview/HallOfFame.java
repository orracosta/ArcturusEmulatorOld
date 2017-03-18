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
        synchronized (this.winners)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT users.look, users.username, users.id, users_settings.hof_points FROM users_settings INNER JOIN users ON users_settings.user_id = users.id WHERE hof_points > 0 ORDER BY hof_points DESC, users.id ASC LIMIT 10"))
            {
                while (set.next())
                {
                    HallOfFameWinner winner = new HallOfFameWinner(set);
                    winners.put(winner.getId(), winner);
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
        return competitionName;
    }

    void setCompetitionName(String name)
    {
        competitionName = name;
    }
}
