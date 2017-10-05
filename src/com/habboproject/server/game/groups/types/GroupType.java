package com.habboproject.server.game.groups.types;

public enum GroupType {
    REGULAR(0), EXCLUSIVE(1), PRIVATE(2);

    private int typeId;

    GroupType(int type) {
        this.typeId = type;
    }

    public int getTypeId() {
        return this.typeId;
    }

    public static GroupType valueOf(int typeId) {
        if (typeId == 0)
            return REGULAR;
        else if (typeId == 1)
            return EXCLUSIVE;
        else
            return PRIVATE;
    }
}
