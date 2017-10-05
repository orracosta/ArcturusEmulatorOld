package com.habboproject.server.game.commands;

import com.habboproject.server.api.networking.messages.IMessageComposer;
import com.habboproject.server.config.Locale;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;


public abstract class ChatCommand {
    public abstract void execute(Session client, String[] params);

    public abstract String getPermission();

    public abstract String getDescription();

    public final IMessageComposer success(String msg) {
        return new AdvancedAlertMessageComposer(Locale.get("command.successful"), msg);
    }

    public static void sendNotif(String msg, Session session) {
        session.send(new NotificationMessageComposer("generic", msg));
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

    public String merge(String[] params, int begin) {
        final StringBuilder mergedParams = new StringBuilder();

        for (int i = 0; i < params.length; i++) {
            if (i >= begin) {
                mergedParams.append(params[i]).append(" ");
            }
        }

        return mergedParams.toString();
    }

    public boolean isHidden() {
        return false;
    }

    public boolean canDisable() {
        return false;
    }

    public boolean isAsync() {
        return false;
    }

    public static class Execution implements Runnable {
        private ChatCommand command;
        private String[] params;
        private Session session;

        public Execution(ChatCommand command, String[] params, Session session) {
            this.command = command;
            this.params = params;
            this.session = session;
        }

        @Override
        public void run() {
            command.execute(session, params);
        }
    }
}
