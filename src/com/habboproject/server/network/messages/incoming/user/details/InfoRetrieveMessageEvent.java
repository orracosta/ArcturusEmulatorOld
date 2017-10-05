package com.habboproject.server.network.messages.incoming.user.details;

import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.messenger.MessengerConfigMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.achievements.AchievementPointsMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.achievements.AchievementRequirementsMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.buildersclub.BuildersClubMembershipMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.details.NewIdentityStatusMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.details.UserObjectMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.BadgeInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.permissions.PerkAllowancesMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;


public class InfoRetrieveMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        client.getPlayer().sendBalance();

        // TODO: Queue these & send all at once.
        client.send(new UserObjectMessageComposer(client.getPlayer()));
        client.send(new BuildersClubMembershipMessageComposer());
        client.send(new PerkAllowancesMessageComposer(client.getPlayer().getData().getRank()));
//        client.send(new CitizenshipStatusMessageComposer());
        client.send(new AchievementPointsMessageComposer(client.getPlayer().getData().getAchievementPoints()));

        client.send(new MessengerConfigMessageComposer());

        client.send(new BadgeInventoryMessageComposer(client.getPlayer().getInventory().getBadges()));
        client.send(new AchievementRequirementsMessageComposer());

        if (client.getPlayer().getData().getNewbieStep().equals("1")) {
            //client.send(new NewIdentityStatusMessageComposer(1));
        }

        client.getPlayer().getMessenger().sendStatus(true);

        client.getPlayer().getMessenger().sendStatus(true, client.getPlayer().getEntity() != null);
    }
}
