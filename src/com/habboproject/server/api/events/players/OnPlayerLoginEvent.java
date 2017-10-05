package com.habboproject.server.api.events.players;

import com.habboproject.server.api.events.Event;
import com.habboproject.server.api.events.players.args.OnPlayerLoginEventArgs;

import java.util.function.Consumer;

public class OnPlayerLoginEvent extends Event<OnPlayerLoginEventArgs> {
    public OnPlayerLoginEvent(Consumer<OnPlayerLoginEventArgs> eventConsumer) {
        super(eventConsumer);
    }
}
