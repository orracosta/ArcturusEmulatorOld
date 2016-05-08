package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 16-11-2014 15:58.
 */
public class InfoDucketsLayout extends CatalogPage
{
    public InfoDucketsLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("info_duckets");
        message.appendInt32(1);
        message.appendString(getHeaderImage());
        message.appendInt32(1);
        message.appendString(getTextOne());
        message.appendInt32(0);
    }
}
