package com.habboproject.server.api.commands;

import com.habboproject.server.api.networking.sessions.BaseSession;

public abstract class ModuleChatCommand {
    public abstract void execute(BaseSession client, String[] args);

    public abstract String getDescription();

    public abstract String getPermission();

    public boolean isHidden() {
        return false;
    }

    public boolean isAsync() {
        return false;
    }

    public final String merge(String[] params, int begin) {
        final StringBuilder mergedParams = new StringBuilder();

        for (int i = 0; i < params.length; i++) {
            if (i >= begin) {
                mergedParams.append(params[i]).append(" ");
            }
        }

        return mergedParams.toString();
    }

    public final String merge(String[] params) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (String s : params) {
            if (!params[params.length - 1].equals(s))
                stringBuilder.append(s).append(" ");
            else
                stringBuilder.append(s);
        }

        return stringBuilder.toString();
    }
}
