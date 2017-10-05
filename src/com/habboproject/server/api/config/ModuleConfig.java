package com.habboproject.server.api.config;

import com.habboproject.server.api.commands.CommandInfo;

import java.util.Map;

public class ModuleConfig {
    private final String name;
    private final String version;
    private final String entryPoint;

    private final Map<String, CommandInfo> commands;

    public ModuleConfig(String name, String version, String entryPoint, Map<String, CommandInfo> commands) {
        this.name = name;
        this.version = version;
        this.entryPoint = entryPoint;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public Map<String, CommandInfo> getCommands() {
        return commands;
    }
}
