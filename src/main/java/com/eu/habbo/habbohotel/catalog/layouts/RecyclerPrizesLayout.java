package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecyclerPrizesLayout extends CatalogPage
{
    public RecyclerPrizesLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendString("recycler_prizes");
        message.appendInt32(3);
        message.appendString("");
        message.appendString("");
        message.appendString("");
        message.appendInt32(3);
        message.appendString("");
        message.appendString("");
        message.appendString("");
    }
}
