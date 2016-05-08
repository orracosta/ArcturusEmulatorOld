package com.eu.habbo.habbohotel.navigation;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 26-8-2014 18:51.
 */
public class PublicRoom {

    public final int id;
    public final int roomId;
    public final int parentId;
    public final String caption;
    public final String description;
    public final String image;
    public final int imageType;

    public PublicRoom(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.parentId = set.getInt("parent_id");
        this.roomId = set.getInt("room_id");
        this.caption = set.getString("caption");
        this.description = set.getString("description");
        this.image = set.getString("image_url");
        this.imageType = set.getString("image_type").equals("large") ? 0 : 1;
    }
}
