package com.habboproject.server.threads.executors.newbie;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.bots.NewbieAI;
import com.habboproject.server.network.messages.outgoing.misc.LinkEventMessageComposer;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/03/2017.
 */
public class NewbieBubbleEvent implements CometThread {
    private final NewbieAI newbieAI;
    private final PlayerEntity playerEntity;
    private final String link;
    private final int step;

    public NewbieBubbleEvent(NewbieAI newbieAI, PlayerEntity playerEntity, String link, int step) {
        this.newbieAI = newbieAI;
        this.playerEntity = playerEntity;
        this.link = link;
        this.step = step;
    }

    @Override
    public void run() {
        playerEntity.getPlayer().getEntity().setOnBubble(step);
        playerEntity.getPlayer().getSession().send(new LinkEventMessageComposer(link));

        if (step == 1) {
            newbieAI.increaseStep();
            newbieAI.setNextStep(true);
        }

        if (step == 0) {
            ThreadManager.getInstance().executeSchedule(new NewbieCompleteInfoEvent(playerEntity.getPlayer()), 2, TimeUnit.SECONDS);
        }
    }
}
