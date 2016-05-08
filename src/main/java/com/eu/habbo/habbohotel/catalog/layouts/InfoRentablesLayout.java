package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 16-11-2014 16:02.
 */
public class InfoRentablesLayout extends CatalogPage
{
    public InfoRentablesLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        String[] data = getTextOne().split("\\|\\|");
        message.appendString("info_rentables");
        message.appendInt32(1);
        message.appendString(getHeaderImage());
        message.appendInt32(data.length);
        for (String d : data) {
            message.appendString(d);
        }
        message.appendInt32(0);
    }
}
