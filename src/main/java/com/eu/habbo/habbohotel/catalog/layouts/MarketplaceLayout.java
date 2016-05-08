package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 1-11-2014 15:58.
 */
public class MarketplaceLayout extends CatalogPage
{
    public MarketplaceLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("marketplace");
        message.appendInt32(0);
        message.appendInt32(0);
    }
}
