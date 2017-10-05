package com.habboproject.server.network.messages.incoming.handshake;

import com.habboproject.server.boot.CometServer;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class CheckReleaseMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) {
        final String release = msg.readString();

        if(!release.equals(CometServer.CLIENT_VERSION)) {
            client.getLogger().warn("Client connected with incorrect client version (" + release + ") and was disposed");
            client.disconnect();
            return;
        }

        if (Session.CLIENT_VERSION == 0) {
            Session.CLIENT_VERSION = this.getReleaseNumber(release);
        } else if (this.getReleaseNumber(release) > Session.CLIENT_VERSION) {
            Session.CLIENT_VERSION = this.getReleaseNumber(release);
        }

        client.getLogger().debug("Client running on release: " + Session.CLIENT_VERSION);
    }

    private int getReleaseNumber(String releaseString) {
        String[] parsedRelease = releaseString.split("-");

        try {
            return Integer.parseInt(parsedRelease[1].substring(0, 8));
        } catch (Exception ignored) {
            return 19700101;
        }
    }
}
