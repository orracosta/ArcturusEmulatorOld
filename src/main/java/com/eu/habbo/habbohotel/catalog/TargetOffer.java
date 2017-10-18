package com.eu.habbo.habbohotel.catalog;


import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TargetOffer implements ISerialize
{
    private final int unknownInt1;
    private final int id;
    private final String identifier;
    private final String unknownString1;
    private final int priceInCredits;
    private final int priceInActivityPoints;
    private final int activityPointsType;
    private final int purchaseLimit;
    private final int expirationTime;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String unknownString2;
    private final int type;
    private final List<String> unknownStringList;

    public TargetOffer(int unknownInt1, int id, String identifier, String unknownString1, int priceInCredits, int priceInActivityPoints, int activityPointsType, int purchaseLimit, int expirationTime, String title, String description, String imageUrl, String unknownString2, int type, List<String> unknownStringList)
    {
        this.unknownInt1 = unknownInt1;
        this.id = id;
        this.identifier = identifier;
        this.unknownString1 = unknownString1;
        this.priceInCredits = priceInCredits;
        this.priceInActivityPoints = priceInActivityPoints;
        this.activityPointsType = activityPointsType;
        this.purchaseLimit = purchaseLimit;
        this.expirationTime = expirationTime;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.unknownString2 = unknownString2;
        this.type = type;
        this.unknownStringList = unknownStringList;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt(this.unknownInt1);
        message.appendInt(this.id);
        message.appendString(this.identifier);
        message.appendString(this.unknownString1);
        message.appendInt(this.priceInCredits);
        message.appendInt(this.priceInActivityPoints);
        message.appendInt(this.activityPointsType);
        message.appendInt(this.purchaseLimit);
        message.appendInt(this.expirationTime);
        message.appendString(this.title);
        message.appendString(this.description);
        message.appendString(this.imageUrl);
        message.appendString(this.unknownString2);
        message.appendInt(this.type);
        message.appendInt(this.unknownStringList.size());
        for (String s : this.unknownStringList)
        {
            message.appendString(s);
        }
    }
}