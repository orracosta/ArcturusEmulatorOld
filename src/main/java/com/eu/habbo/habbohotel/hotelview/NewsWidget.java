package com.eu.habbo.habbohotel.hotelview;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsWidget
{
    /**
     * News ID
     */
    private int id;

    /**
     * Title
     */
    private String title;

    /**
     * Message
     */
    private String message;

    /**
     * Text on the button
     */
    private String buttonMessage;

    /**
     * Type
     */
    private int type;

    /**
     * Link
     */
    private String link;

    /**
     * Image
     */
    private String image;

    public NewsWidget(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.title = set.getString("title");
        this.message = set.getString("text");
        this.buttonMessage = set.getString("button_text");
        this.type = set.getString("button_type").equals("client") ? 1 : 0;
        this.link = set.getString("button_link");
        this.image = set.getString("image");
    }

    /**
     * News ID
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Message
     */
    public String getMessage()
    {
        return this.message;
    }

    /**
     * Text on the button
     */
    public String getButtonMessage()
    {
        return this.buttonMessage;
    }

    /**
     * Type
     */
    public int getType()
    {
        return this.type;
    }

    /**
     * Link
     */
    public String getLink()
    {
        return this.link;
    }

    /**
     * Image
     */
    public String getImage()
    {
        return this.image;
    }
}
