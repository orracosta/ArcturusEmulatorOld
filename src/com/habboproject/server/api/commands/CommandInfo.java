package com.habboproject.server.api.commands;

public class CommandInfo {
    private final String description;
    private final String permission;

    public CommandInfo(String description, String permission) {
        this.description = description;
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }
}
