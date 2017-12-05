package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.Emulator;
import gnu.trove.set.hash.THashSet;

import java.sql.*;

public class NewsList
{
    private final THashSet<NewsWidget> newsWidgets;

    public NewsList()
    {
        this.newsWidgets = new THashSet<NewsWidget>();
        this.reload();
    }

    /**
     * Reloads the news.
     */
    public void reload()
    {
        synchronized (this.newsWidgets)
        {
            this.newsWidgets.clear();
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM hotelview_news ORDER BY id DESC LIMIT 10"))
            {
                while (set.next())
                {
                    this.newsWidgets.add(new NewsWidget(set));
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    /**
     * The newsitems displayed on the HotelView
     * @return
     */
    public THashSet<NewsWidget> getNewsWidgets()
    {
        return newsWidgets;
    }
}
