package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 16-11-2014 16:01.
 */
public class InfoPetsLayout extends CatalogPage
{
    public InfoPetsLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("info_pets");
        message.appendInt32(2);
        message.appendString(getHeaderImage());
        message.appendString(getTeaserImage());
        message.appendInt32(3);
        message.appendString(getTextOne());
        message.appendString("");
        message.appendString(getTextTeaser());
        message.appendInt32(0);
    }
}
