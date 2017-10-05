package com.habboproject.server.threads.executors.newbie;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.outgoing.misc.LinkEventMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.details.NewIdentityStatusMessageComposer;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 05/03/2017.
 */
public class NewbieLobbyOfferEvent implements CometThread {
    private final PlayerEntity playerEntity;

    public NewbieLobbyOfferEvent(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        this.playerEntity.getPlayer().getSession().send(new LinkEventMessageComposer("nux/lobbyoffer/hide"));
        this.playerEntity.getPlayer().getSession().send(new NewIdentityStatusMessageComposer(2));
        this.playerEntity.getPlayer().getSession().send(new LinkEventMessageComposer("nux/lobbyoffer/show"));
    }
}
