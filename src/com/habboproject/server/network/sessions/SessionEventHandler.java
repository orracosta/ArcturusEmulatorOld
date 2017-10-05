package com.habboproject.server.network.sessions;

import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.protocol.headers.Events;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.util.HashMap;
import java.util.Map;


public class SessionEventHandler {
    /**
     * The session attached to the event handler
     */
    private final Session session;

    /**
     * The events required to complete login
     */
    private final Map<Integer, Boolean> loginEvents;

    /**
     * Initialize the event handler
     *
     * @param session The session attached to the event handler.
     */
    public SessionEventHandler(Session session) {
        this.session = session;

        this.loginEvents = new HashMap<Integer, Boolean>() {{
            put((int) Events.GenerateSecretKeyMessageEvent, true);
            put((int) Events.InitCryptoMessageEvent, true);
            put((int) Events.SSOTicketMessageEvent, true);
        }};
    }

    /**
     * Handle the incoming event
     *
     * @param msg The incoming event
     */
    public void handle(MessageEvent msg) {
        // Checks if the event is a login event, if it is then check if it is enabled, if it is then disable it because the
        // event can only be called once!
        if (this.loginEvents.containsKey(msg.getId())) {
            if (!this.loginEvents.get(msg.getId())) {
                return; // event not allowed
            } else {
                this.loginEvents.replace(msg.getId(), false);
            }
        }

        NetworkManager.getInstance().getMessages().handle(msg, this.session);
    }

    /**
     * Clean up the event handler
     */
    public void dispose() {
        this.loginEvents.clear();
    }
}
