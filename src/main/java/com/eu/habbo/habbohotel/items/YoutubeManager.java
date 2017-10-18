package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.ArrayList;

public class YoutubeManager
{
    public THashMap<Integer, ArrayList<YoutubeItem>> playLists = new THashMap<Integer, ArrayList<YoutubeItem>>();
    public THashMap<Integer, YoutubeItem> videos = new THashMap<Integer, YoutubeItem>();

    public void load()
    {
        this.videos.clear();
        this.playLists.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (Statement statement = connection.createStatement())
            {
                try (ResultSet set = statement.executeQuery("SELECT * FROM youtube_items"))
                {
                    while (set.next())
                    {
                        this.videos.put(set.getInt("id"), new YoutubeItem(set));
                    }
                }

                try (ResultSet set = statement.executeQuery("SELECT * FROM youtube_playlists ORDER BY `order` ASC"))
                {
                    while (set.next())
                    {
                        if (!this.playLists.containsKey(set.getInt("item_id")))
                        {
                            this.playLists.put(set.getInt("item_id"), new ArrayList<YoutubeItem>());
                        }

                        YoutubeItem item = this.videos.get(set.getInt("video_id"));

                        if (item != null)
                        {
                            this.playLists.get(set.getInt("item_id")).add(item);
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public ArrayList<YoutubeItem> getPlaylist(Item item)
    {
        if (this.playLists.containsKey(item.getId()))
        {
            return this.playLists.get(item.getId());
        }

        return new ArrayList<YoutubeItem>();
    }

    public YoutubeItem getVideo(Item item, String video)
    {
        if (this.playLists.contains(item.getId()))
        {
            for (YoutubeItem v : this.playLists.get(item.getId()))
            {
                if (v.video.equalsIgnoreCase(video))
                {
                    return v;
                }
            }
        }

        return null;
    }

    public String getPreviewImage(Item item)
    {
        if (this.playLists.contains(item.getId()))
        {
            if (!this.playLists.get(item.getId()).isEmpty())
            {
                return this.playLists.get(item.getId()).get(0).video;
            }
        }

        return "";
    }

    public YoutubeItem getVideo(Item item, int index)
    {
        if (this.playLists.containsKey(item.getId()))
        {
            return this.playLists.get(item.getId()).get(index);
        }

        return null;
    }

    public class YoutubeItem
    {
        public final int id;
        public final String video;
        public final String title;
        public final String description;
        public final int startTime;
        public final int endTime;

        public YoutubeItem(ResultSet set) throws SQLException
        {
            this.id = set.getInt("id");
            this.video = set.getString("video");
            this.title = set.getString("title");
            this.description = set.getString("description");
            this.startTime = set.getInt("start_time");
            this.endTime = set.getInt("end_time");
        }
    }
}