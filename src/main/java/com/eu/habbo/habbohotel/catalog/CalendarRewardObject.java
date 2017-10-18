package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CalendarRewardObject
{
    private final int id;
    private final String name;
    private final String customImage;
    private final int credits;
    private final int points;
    private final int pointsType;
    private final String badge;
    private final int catalogItemId;

    public CalendarRewardObject(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.name = set.getString("name");
        this.customImage = set.getString("custom_image");
        this.credits = set.getInt("credits");
        this.points = set.getInt("points");
        this.pointsType = set.getInt("points_type");
        this.badge = set.getString("badge");
        this.catalogItemId = set.getInt("catalog_item_id");
    }

    public void give(Habbo habbo)
    {
        if (this.credits > 0)
        {
            habbo.giveCredits(this.credits);
        }

        if (this.points > 0)
        {
            habbo.givePoints(this.pointsType, this.points);
        }

        if (!this.badge.isEmpty())
        {
            habbo.addBadge(this.badge);
        }

        if (this.catalogItemId > 0)
        {
            CatalogItem item = this.getCatalogItem();

            if (item != null)
            {
                Emulator.getGameEnvironment().getCatalogManager().purchaseItem(null, item, habbo, 1, "", true);
            }
        }
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getCustomImage()
    {
        return this.customImage;
    }

    public int getCredits()
    {
        return this.credits;
    }

    public int getPoints()
    {
        return this.points;
    }

    public int getPointsType()
    {
        return this.pointsType;
    }

    public String getBadge()
    {
        return this.badge;
    }

    public CatalogItem getCatalogItem()
    {
        return Emulator.getGameEnvironment().getCatalogManager().getCatalogItem(this.catalogItemId);
    }
}
