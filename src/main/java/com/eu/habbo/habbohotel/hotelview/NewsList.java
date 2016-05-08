package com.eu.habbo.habbohotel.hotelview;

import com.eu.habbo.Emulator;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 27-8-2014 10:17.
 */
public class NewsList {

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
        this.newsWidgets.clear();

        THashSet<NewsWidget> news = new THashSet<NewsWidget>();
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM hotelview_news ORDER BY id DESC LIMIT 10");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                news.add(new NewsWidget(set));
            }
            set.close();
            statement.close();
            statement.getConnection().close();

            this.newsWidgets.addAll(news);
            news.clear();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
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
