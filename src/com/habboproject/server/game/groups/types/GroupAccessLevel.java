package com.habboproject.server.game.groups.types;

public enum GroupAccessLevel {
    MEMBER, ADMIN, OWNER;

    public boolean isAdmin() {
        return this.equals(ADMIN) || this.equals(OWNER);
    }
}
