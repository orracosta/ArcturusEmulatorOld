package com.eu.habbo.habbohotel.users;

/**
 * Created on 13-9-2014 17:40.
 */
public enum DanceType {
    NONE(0),
    HAB_HOP(1),
    POGO_MOGO(2),
    DUCK_FUNK(3),
    THE_ROLLIE(4);

    private final int type;

    DanceType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return this.type;
    }
}
