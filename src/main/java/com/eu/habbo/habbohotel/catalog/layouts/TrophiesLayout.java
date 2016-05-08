package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 6-10-2014 21:09.
 */
public class TrophiesLayout extends CatalogPage {

    public TrophiesLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public void serialize(ServerMessage message) {
        message.appendString("trophies");
        message.appendInt32(3);
        message.appendString(super.getHeaderImage());
        message.appendString(super.getTeaserImage());
        message.appendString(super.getSpecialImage());
        message.appendInt32(3);
        message.appendString(super.getTextOne());
        message.appendString(super.getTextDetails());
        message.appendString(super.getTextTeaser());
    }
}
