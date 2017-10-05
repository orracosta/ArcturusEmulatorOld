package com.habboproject.server.network.messages.outgoing.user.inventory;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.HashMap;
import java.util.Map;


public class BadgeInventoryMessageComposer extends MessageComposer {

    private final Map<String, Integer> badges;

    public BadgeInventoryMessageComposer(final Map<String, Integer> badges) {
        this.badges = badges;
    }

    @Override
    public short getId() {
        return Composers.BadgesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        Map<String, Integer> activeBadges = new HashMap<>();

        msg.writeInt(badges.size());

        badges.forEach((badge, slot) -> {
            if (slot > 0) {
                activeBadges.put(badge, slot);
            }

            msg.writeInt(0);
            msg.writeString(badge);
        });

        msg.writeInt(activeBadges.size());

        activeBadges.forEach((k, v) -> {
            msg.writeInt(v);
            msg.writeString(k);
        });

        activeBadges.clear();
    }
}
