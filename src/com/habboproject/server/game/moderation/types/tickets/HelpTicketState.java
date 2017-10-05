package com.habboproject.server.game.moderation.types.tickets;

public enum HelpTicketState {
    OPEN(1),
    IN_PROGRESS(2),
    CLOSED(0),
    INVALID(0),
    RESOLVED(0),
    ABUSIVE(0);

    private int tabId;

    HelpTicketState(int tabId) {
        this.tabId = tabId;
    }

    public int getTabId() {
        return this.tabId;
    }
}
