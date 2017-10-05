package com.habboproject.server.game.groups.types.components.forum.settings;

public enum ForumPermission {
    EVERYBODY(0),
    MEMBERS(1),
    ADMINISTRATORS(2),
    OWNER(3);

    private int permissionId;

    ForumPermission(int permissionId) {
        this.permissionId = permissionId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public static ForumPermission getById(int id) {
        switch(id) {
            case 0: return EVERYBODY;
            case 1: return MEMBERS;
            case 2: return ADMINISTRATORS;
            case 3: return OWNER;
        }

        return MEMBERS;
    }
}

