package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 25-8-2015 14:16.
 */
public class TraxLayout extends CatalogPage
{
    public TraxLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("soundmachine");
        message.appendInt32(2);
        message.appendString(super.getHeaderImage());
        message.appendString(super.getTeaserImage());
        message.appendInt32(2);
        message.appendString(super.getTextOne());
        message.appendString(super.getTextDetails());
    }
}
