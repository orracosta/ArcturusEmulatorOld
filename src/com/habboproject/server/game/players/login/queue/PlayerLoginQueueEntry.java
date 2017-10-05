package com.habboproject.server.game.players.login.queue;

import com.habboproject.server.network.sessions.Session;


public class PlayerLoginQueueEntry {
    private Session connectingClient;

    private int playerId;
    private String ssoTicket;

    public PlayerLoginQueueEntry(Session client, int id, String sso) {
        this.connectingClient = client;

        this.playerId = id;
        this.ssoTicket = sso;
    }

    public Session getClient() {
        return this.connectingClient;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getSsoTicket() {
        return ssoTicket;
    }
}
