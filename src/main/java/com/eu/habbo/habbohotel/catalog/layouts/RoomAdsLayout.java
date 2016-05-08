package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 15-10-2014 10:05.
 */
public class RoomAdsLayout extends CatalogPage
{
    public RoomAdsLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("roomads");
        message.appendInt32(2);
        message.appendString(super.getHeaderImage());
        message.appendString(super.getTeaserImage());
        message.appendInt32(2);
        message.appendString(super.getTextOne());
        message.appendString(super.getTextTwo());
    }
}
