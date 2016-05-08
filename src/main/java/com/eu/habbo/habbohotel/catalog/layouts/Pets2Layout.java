package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 6-12-2014 16:20.
 */
public class Pets2Layout extends CatalogPage
{
    public Pets2Layout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("pets2");
        message.appendInt32(2);
        message.appendString(super.getHeaderImage());
        message.appendString(super.getTeaserImage());
        message.appendInt32(4);
        message.appendString(super.getTextOne());
        message.appendString(super.getTextTwo());
        message.appendString(super.getTextDetails());
        message.appendString(super.getTextTeaser());
    }
}
